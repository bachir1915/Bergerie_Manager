#!/usr/bin/env python3
"""
Script d'entraînement du modèle Isolation Forest pour la détection d'anomalies
de comportement et de santé des animaux (ovins) à partir de données capteurs ESP32.

Features utilisées :
1. heure_jour (0-23) : capture le rythme circadien jour/nuit
2. temperature_ambiante (°C) : capteur DHT22 / environnement
3. humidite (%) : capteur DHT22
4. qualite_air (AQI / PPM) : capteur de gaz / air
5. distance_parcourue_km : cumul horaire calculé à partir du GPS
6. vitesse_moyenne_kmh : vitesse moyenne horaire issue du GPS
"""

import os
from pathlib import Path
import numpy as np
import pandas as pd
from sklearn.ensemble import IsolationForest
from sklearn.preprocessing import StandardScaler
import joblib

# Fixer la graine aléatoire pour la reproductibilité
np.random.seed(42)

FEATURE_COLUMNS = [
    "heure_jour",
    "temperature_ambiante",
    "humidite",
    "qualite_air",
    "distance_parcourue_km",
    "vitesse_moyenne_kmh",
]


def generer_dataset_simule(n_echantillons: int = 4000) -> pd.DataFrame:
    """
    Génère un jeu de données réaliste modélisant le cycle nycthéméral (jour/nuit)
    d'un troupeau de brebis avec injection d'anomalies comportementales et environnementales.
    """
    heures = np.random.randint(0, 24, size=n_echantillons)
    
    temperatures = []
    humidites = []
    qualites_air = []
    distances = []
    vitesses = []
    labels_vrais = []  # 0 = normal, 1 = anomalie injectée

    for h in heures:
        est_jour = 7 <= h <= 19

        if est_jour:
            # Comportement normal de jour : pâturage, déplacement actif
            temp = np.random.normal(loc=23.0, scale=3.0)
            hum = np.random.normal(loc=55.0, scale=6.0)
            air = np.random.normal(loc=35.0, scale=7.0)
            dist = np.random.uniform(0.6, 2.0)
            vit = np.random.uniform(0.7, 2.5)
        else:
            # Comportement normal de nuit : repos, rumination, immobilité normale
            temp = np.random.normal(loc=13.0, scale=2.0)
            hum = np.random.normal(loc=78.0, scale=5.0)
            air = np.random.normal(loc=22.0, scale=5.0)
            dist = np.random.uniform(0.00, 0.03)
            vit = np.random.uniform(0.00, 0.05)

        temperatures.append(temp)
        humidites.append(hum)
        qualites_air.append(air)
        distances.append(dist)
        vitesses.append(vit)
        labels_vrais.append(0)

    # Convertir en DataFrame
    df = pd.DataFrame({
        "heure_jour": heures,
        "temperature_ambiante": np.clip(temperatures, 5.0, 35.0),
        "humidite": np.clip(humidites, 20.0, 95.0),
        "qualite_air": np.clip(qualites_air, 10.0, 70.0),
        "distance_parcourue_km": np.clip(distances, 0.0, 3.0),
        "vitesse_moyenne_kmh": np.clip(vitesses, 0.0, 4.0),
        "est_anomalie_reelle": labels_vrais
    })

    # --- INJECTION D'ANOMALIES RÉALISTES (~10% du jeu de données) ---
    n_anomalies = int(n_echantillons * 0.10)
    indices_anomalies = np.random.choice(df.index, size=n_anomalies, replace=False)

    for idx in indices_anomalies:
        type_ano = np.random.choice(
            ["immobile_jour", "fuite_nuit", "stress_thermique", "air_toxique"],
            p=[0.40, 0.25, 0.20, 0.15]
        )
        df.loc[idx, "est_anomalie_reelle"] = 1

        if type_ano == "immobile_jour":
            # 1. Immobilité diurne suspecte (mouton prostré, blessure, piétin sévère en plein midi)
            df.loc[idx, "heure_jour"] = np.random.randint(10, 18)
            df.loc[idx, "distance_parcourue_km"] = 0.00
            df.loc[idx, "vitesse_moyenne_kmh"] = 0.00
            df.loc[idx, "temperature_ambiante"] = np.random.uniform(22.0, 29.0)

        elif type_ano == "fuite_nuit":
            # 2. Fuite nocturne / panique / prédation / vol à 2h du matin
            df.loc[idx, "heure_jour"] = np.random.randint(1, 5)
            df.loc[idx, "distance_parcourue_km"] = np.random.uniform(3.0, 6.5)
            df.loc[idx, "vitesse_moyenne_kmh"] = np.random.uniform(6.0, 13.0)

        elif type_ano == "stress_thermique":
            # 3. Canicule extrême / coup de chaleur dans la bergerie
            df.loc[idx, "temperature_ambiante"] = np.random.uniform(41.0, 49.0)
            df.loc[idx, "humidite"] = np.random.uniform(88.0, 98.0)
            df.loc[idx, "distance_parcourue_km"] = np.random.uniform(0.05, 0.2)

        elif type_ano == "air_toxique":
            # 4. Qualité de l'air très dégradée (accumulation ammoniac / fumée / incendie)
            df.loc[idx, "qualite_air"] = np.random.uniform(190.0, 320.0)
            df.loc[idx, "temperature_ambiante"] = np.random.uniform(32.0, 42.0)

    return df


def entrainer_et_sauvegarder():
    print("=" * 65)
    print(" Entraînement du modèle d'Isolation Forest (Détection d'Anomalies)")
    print("=" * 65)

    # 1. Génération des données
    print("1. Génération du dataset synthétique avec cycle nycthéméral...")
    df = generer_dataset_simule(n_echantillons=6000)
    print(f"   -> {len(df)} enregistrements générés dont {df['est_anomalie_reelle'].sum()} anomalies injectées.")

    X = df[FEATURE_COLUMNS]

    # 2. Standardisation des variables avec StandardScaler
    print("2. Ajustement du StandardScaler sur les features...")
    scaler = StandardScaler()
    X_scaled = scaler.fit_transform(X)

    # 3. Entraînement de l'Isolation Forest
    print("3. Entraînement de l'Isolation Forest (contamination=0.15)...")
    model = IsolationForest(
        n_estimators=250,
        contamination=0.15,
        random_state=42,
        n_jobs=-1
    )
    model.fit(X_scaled)

    # 4. Validation des prédictions (1 = inlier/normal, -1 = outlier/anomalie)
    preds = model.predict(X_scaled)
    anomalies_detectees = (preds == -1)
    
    # Matrice de détection sur les anomalies réelles injectées
    vrais_positifs = np.sum((anomalies_detectees == 1) & (df["est_anomalie_reelle"] == 1))
    faux_positifs = np.sum((anomalies_detectees == 1) & (df["est_anomalie_reelle"] == 0))
    total_anomalies_reelles = df["est_anomalie_reelle"].sum()

    print(f"4. Résultats de détection :")
    print(f"   - Anomalies détectées par le modèle : {np.sum(anomalies_detectees)} ({np.mean(anomalies_detectees)*100:.1f}%)")
    print(f"   - Taux de rappel des anomalies injectées : {vrais_positifs}/{total_anomalies_reelles} ({vrais_positifs/total_anomalies_reelles*100:.1f}%)")
    print(f"   - Faux positifs sur données saines : {faux_positifs}")

    # 5. Sauvegarde du modèle et du scaler
    base_dir = Path(__file__).resolve().parent
    app_dir = base_dir / "app"
    app_dir.mkdir(parents=True, exist_ok=True)

    model_path = app_dir / "model.joblib"
    scaler_path = app_dir / "scaler.joblib"

    print("5. Sauvegarde des artefacts...")
    joblib.dump(model, model_path)
    joblib.dump(scaler, scaler_path)

    print(f"    Modèle sauvegardé dans : {model_path}")
    print(f"    Scaler sauvegardé dans : {scaler_path}")

    # 6. Test rapide de cas concrets
    print("\n" + "-" * 65)
    print("[TEST] Test de validation sur cas reels :")
    
    cas_tests = [
        ("Jour normal (14h, marche 1.1km)", [14, 24.0, 52.0, 30.0, 1.1, 1.4]),
        ("Nuit normale (02h, dort 0.02km)", [2, 12.0, 78.0, 22.0, 0.02, 0.04]),
        ("ANOMALIE : Immobile de jour (14h, 0km)", [14, 25.0, 50.0, 32.0, 0.00, 0.00]),
        ("ANOMALIE : Fuite nocturne (03h, 4.2km)", [3, 11.0, 75.0, 25.0, 4.2, 8.5]),
        ("ANOMALIE : Canicule/Air toxique (15h)", [15, 45.0, 92.0, 260.0, 0.1, 0.1])
    ]

    for label, raw_vals in cas_tests:
        vals_scaled = scaler.transform([raw_vals])
        pred = model.predict(vals_scaled)[0]
        score = -float(model.score_samples(vals_scaled)[0])
        est_ano = (pred == -1)
        statut = "ANOMALIE" if est_ano else "NORMAL"
        print(f"   [{statut}] {label} -> score={score:.3f}")

    print("=" * 65)
    print("[OK] Entrainement termine avec succes !")


if __name__ == "__main__":
    entrainer_et_sauvegarder()
