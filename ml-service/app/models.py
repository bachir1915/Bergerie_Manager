from typing import Optional, List
from pydantic import BaseModel, Field


class CapteurLectureInput(BaseModel):
    """
    Données horaires agrégées issues des capteurs ESP32 pour un animal donné.
    Les noms de champs sont en snake_case pour l'interfaçage JSON standard.
    """
    animal_id: int = Field(..., description="Identifiant unique de l'animal", example=42)
    heure_jour: int = Field(..., ge=0, le=23, description="Heure de la journée (0-23)", example=14)
    temperature_ambiante: float = Field(..., description="Température ambiante en °C", example=24.5)
    humidite: float = Field(..., description="Humidité relative de l'air en %", example=58.0)
    qualite_air: float = Field(..., description="Qualité de l'air (indice AQI ou PPM CO2/COV)", example=35.0)
    distance_parcourue_km: float = Field(..., ge=0.0, description="Distance totale parcourue sur la dernière heure en km", example=0.85)
    vitesse_moyenne_kmh: float = Field(..., ge=0.0, description="Vitesse moyenne sur la dernière heure en km/h", example=1.2)


class PredictionAnomalieOutput(BaseModel):
    """
    Résultat de la détection d'anomalies par Isolation Forest.
    """
    animal_id: int = Field(..., description="Identifiant unique de l'animal")
    score_anomalie: float = Field(..., description="Score d'anomalie normalisé (plus le score est élevé, plus le comportement est atypique)")
    est_anomalie: bool = Field(..., description="True si le comportement est classé comme anomalie")
    niveau_risque: str = Field(..., description="Niveau de gravité : NORMAL, MODERE, ELEVE, CRITIQUE")
    details: Optional[str] = Field(None, description="Explication contextuelle du diagnostic")


class HealthResponse(BaseModel):
    """
    Statut de santé du microservice et chargement des artefacts ML.
    """
    status: str
    model_loaded: bool
    scaler_loaded: bool
    features: List[str]
1