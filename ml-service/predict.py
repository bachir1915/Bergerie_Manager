#!/usr/bin/env python3
"""
Script simple pour charger le modèle Isolation Forest entraîné et le scaler,
et prédire si un nouvel enregistrement est une anomalie.
Utilisation :
    python predict.py <heure> <temp> <hum> <qualite> <dist> <vitesse>
Exemple :
    python predict.py 14 24.0 52.0 30.0 1.1 1.4
"""

import sys
from pathlib import Path
import joblib
import numpy as np

BASE_DIR = Path(__file__).resolve().parent
APP_DIR = BASE_DIR / "app"
MODEL_PATH = APP_DIR / "model.joblib"
SCALER_PATH = APP_DIR / "scaler.joblib"

model = joblib.load(MODEL_PATH)
scaler = joblib.load(SCALER_PATH)


def predict_anomaly(raw_vals):
    """Prédire le statut d'anomalie.

    Args:
        raw_vals (list[float]): Liste des 6 caractéristiques dans l'ordre
            [heure_jour, temperature_ambiante, humidite, qualite_air,
             distance_parcourue_km, vitesse_moyenne_kmh]
    Returns:
        tuple[str, float]: ('ANOMALIE' ou 'NORMAL', score)
    """
    vals_scaled = scaler.transform([raw_vals])
    pred = model.predict(vals_scaled)[0]
    score = -float(model.score_samples(vals_scaled)[0])
    status = "ANOMALIE" if pred == -1 else "NORMAL"
    return status, score


if __name__ == "__main__":
    if len(sys.argv) != 7:
        print("Usage: python predict.py <heure> <temp> <hum> <qualite> <dist> <vitesse>")
        sys.exit(1)
    try:
        inputs = [float(arg) for arg in sys.argv[1:]]
    except ValueError:
        print("Erreur : tous les paramètres doivent être numériques.")
        sys.exit(1)
    statut, score = predict_anomaly(inputs)
    print(f"[{statut}] score={score:.3f}")
