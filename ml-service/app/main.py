import logging
from contextlib import asynccontextmanager
from pathlib import Path
from typing import Optional

from fastapi import FastAPI, HTTPException, status
import joblib
import numpy as np
import pandas as pd

from app.models import (
    CapteurLectureInput,
    PredictionAnomalieOutput,
    HealthResponse,
)

# Configuration du logging
logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(message)s")
logger = logging.getLogger("ml_service")

# Définition des features ordonnées requises par le modèle
FEATURE_COLUMNS = [
    "heure_jour",
    "temperature_ambiante",
    "humidite",
    "qualite_air",
    "distance_parcourue_km",
    "vitesse_moyenne_kmh",
]

# Variables globales pour le stockage en mémoire 
model = None
scaler = None
model_loaded = False
scaler_loaded = False


def charger_artefacts():
    """
    Charge le modèle IsolationForest et le StandardScaler depuis le disque.
    """
    global model, scaler, model_loaded, scaler_loaded
    current_dir = Path(__file__).resolve().parent
    model_path = current_dir / "model.joblib"
    scaler_path = current_dir / "scaler.joblib"

    if model_path.exists() and scaler_path.exists():
        try:
            model = joblib.load(model_path)
            scaler = joblib.load(scaler_path)
            model_loaded = True
            scaler_loaded = True
            logger.info(" Modèle et Scaler chargés avec succès depuis %s", current_dir)
        except Exception as e:
            logger.error(" Erreur lors du chargement des artefacts : %s", e)
            model_loaded = False
            scaler_loaded = False
    else:
        logger.warning(
            " Artefacts introuvables (%s ou %s). Lancez 'python train_model.py' pour les générer.",
            model_path.name,
            scaler_path.name,
        )
        model_loaded = False
        scaler_loaded = False


@asynccontextmanager
async def lifespan(app: FastAPI):
    """
    Gestionnaire de cycle de vie FastAPI : charge les artefacts au démarrage.
    """
    logger.info("Démarrage du microservice de prédiction d'anomalies...")
    charger_artefacts()
    yield
    logger.info("Arrêt du microservice de prédiction d'anomalies.")


app = FastAPI(
    title="Service de Détection d'Anomalies Ovines (IoT)",
    description="Microservice FastAPI basé sur scikit-learn IsolationForest pour détecter les comportements et anomalies de santé à partir des capteurs ESP32.",
    version="1.0.0",
    lifespan=lifespan,
)


@app.get("/health", response_model=HealthResponse, tags=["Monitoring"])
def health_check():
    """
    Vérifie l'état du microservice et la présence du modèle entraîné en mémoire.
    """
    return HealthResponse(
        status="ok" if (model_loaded and scaler_loaded) else "degraded",
        model_loaded=model_loaded,
        scaler_loaded=scaler_loaded,
        features=FEATURE_COLUMNS,
    )


@app.post("/predict", response_model=PredictionAnomalieOutput, tags=["Prediction"])
def predire_anomalie(lecture: CapteurLectureInput):
    """
    Reçoit les lectures brutes agrégées de la dernière heure pour un animal
    et renvoie le score d'anomalie, la décision binaire et le niveau de risque.
    """
    # 1. Vérification de la disponibilité du modèle (HTTP 503 si non prêt)
    if not (model_loaded and scaler_loaded and model is not None and scaler is not None):
        logger.error("Requête /predict reçue mais le modèle n'est pas disponible.")
        raise HTTPException(
            status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
            detail="Le modèle d'anomalie n'est pas encore entraîné ou chargé. Exécutez 'python train_model.py'.",
        )

    try:
        # 2. Préparation du vecteur de features avec les noms exacts
        vecteur = pd.DataFrame(
            [[
                lecture.heure_jour,
                lecture.temperature_ambiante,
                lecture.humidite,
                lecture.qualite_air,
                lecture.distance_parcourue_km,
                lecture.vitesse_moyenne_kmh,
            ]],
            columns=FEATURE_COLUMNS,
        )

        # 3. Standardisation via le scaler entraîné
        vecteur_scaled = scaler.transform(vecteur)

        # 4. Prédiction Isolation Forest
        # predict() renvoie 1 pour inlier (normal) et -1 pour outlier (anomalie)
        pred = model.predict(vecteur_scaled)[0]
        est_anomalie = bool(pred == -1)

        # score_samples renvoie l'opposé du score d'anomalie : plus c'est négatif, plus c'est anormal.
        # On normalise en score positif pour l'utilisateur
        raw_score = -float(model.score_samples(vecteur_scaled)[0])
        score_anomalie = round(raw_score, 4)

        # 5. Diagnostic contextuel et qualification du risque
        if not est_anomalie:
            niveau_risque = "NORMAL"
            details = "Activité normale et constantes d'enclos stables."
        else:
            # Qualification métier de l'anomalie
            h = lecture.heure_jour
            dist = lecture.distance_parcourue_km
            vit = lecture.vitesse_moyenne_kmh
            temp = lecture.temperature_ambiante
            air = lecture.qualite_air

            if temp >= 40.0 or air >= 180.0:
                niveau_risque = "CRITIQUE"
                details = f"Alerte environnementale : température critique ({temp}°C) ou qualité d'air toxique ({air} AQI)."
            elif (h < 6 or h >= 21) and dist >= 1.0:
                niveau_risque = "CRITIQUE"
                details = f"Activité nocturne anormale ({dist:.2f} km à {h:02d}h) : suspicion de fuite, panique ou vol."
            elif 7 <= h <= 18 and dist <= 0.05 and vit <= 0.05:
                niveau_risque = "ELEVE"
                details = f"Immobilité diurne suspecte à {h:02d}h (distance {dist:.2f} km) : suspicion de prostration, blessure ou piétin."
            elif score_anomalie >= 0.65:
                niveau_risque = "CRITIQUE"
                details = "Baisse marquée de vitalité et comportement inhabituel nécessitant un contrôle vétérinaire rapide."
            elif score_anomalie >= 0.56:
                niveau_risque = "ELEVE"
                details = "Comportement inhabituel nécessitant une visite d'observation en enclos."
            else:
                niveau_risque = "MODERE"
                details = "Légère variation d'activité constatée, surveillance recommandée."

        logger.info(
            "Prédiction Animal %d (h=%dh, d=%.2fkm, temp=%.1f°C) -> Anomalie=%s [%s] (score=%.3f)",
            lecture.animal_id,
            lecture.heure_jour,
            lecture.distance_parcourue_km,
            lecture.temperature_ambiante,
            est_anomalie,
            niveau_risque,
            score_anomalie,
        )

        return PredictionAnomalieOutput(
            animal_id=lecture.animal_id,
            score_anomalie=score_anomalie,
            est_anomalie=est_anomalie,
            niveau_risque=niveau_risque,
            details=details,
        )

    except Exception as e:
        logger.exception("Erreur inattendue lors de la prédiction : %s", e)
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Erreur interne lors du traitement : {str(e)}",
        )
