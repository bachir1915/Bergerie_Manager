"""
Tests unitaires et d'intégration du microservice FastAPI avec TestClient.
Permet de valider :
1. Le point de terminaison /health
2. Un comportement diurne normal (est_anomalie=False)
3. Un comportement nocturne normal (est_anomalie=False)
4. Une immobilité suspecte de jour (est_anomalie=True)
5. Une fuite/vol nocturne (est_anomalie=True)
6. Une alerte environnementale extrême (température/air critique)
"""

import sys
from pathlib import Path
from fastapi.testclient import TestClient

# Ajouter le répertoire ml-service au sys.path
BASE_DIR = Path(__file__).resolve().parent
sys.path.insert(0, str(BASE_DIR))

from app.main import app, charger_artefacts


def test_suite():
    print("=" * 60)
    print(" [TESTS] Lancement de la suite de tests du microservice ML")
    print("=" * 60)

    # 1. Vérifier que le modèle est chargé
    charger_artefacts()
    client = TestClient(app)

    # Test 1 : Health Check
    res_health = client.get("/health")
    print(f"\n[TEST 1] GET /health -> Status code: {res_health.status_code}")
    assert res_health.status_code == 200
    data = res_health.json()
    print(f"         model_loaded: {data.get('model_loaded')}, features: {data.get('features')}")
    assert data.get("model_loaded") is True, "Le modèle doit être chargé pour continuer les tests."

    # Test 2 : Lecture normale en pleine journée (14h, marche 1.2km, temp 24°C)
    payload_normal_jour = {
        "animal_id": 101,
        "heure_jour": 14,
        "temperature_ambiante": 24.0,
        "humidite": 55.0,
        "qualite_air": 35.0,
        "distance_parcourue_km": 1.2,
        "vitesse_moyenne_kmh": 1.5,
    }
    res_norm = client.post("/predict", json=payload_normal_jour)
    assert res_norm.status_code == 200
    data_norm = res_norm.json()
    print(f"\n[TEST 2] Cas Normal Jour : {payload_normal_jour}")
    print(f"         Résultat -> est_anomalie={data_norm['est_anomalie']}, risque={data_norm['niveau_risque']}, score={data_norm['score_anomalie']}")
    assert data_norm["est_anomalie"] is False, "Une marche normale de jour ne doit pas être une anomalie."
    assert data_norm["niveau_risque"] == "NORMAL"

    # Test 3 : Lecture normale la nuit (02h, dort 0.02km, temp 13°C)
    payload_normal_nuit = {
        "animal_id": 102,
        "heure_jour": 2,
        "temperature_ambiante": 13.0,
        "humidite": 78.0,
        "qualite_air": 25.0,
        "distance_parcourue_km": 0.02,
        "vitesse_moyenne_kmh": 0.03,
    }
    res_norm_nuit = client.post("/predict", json=payload_normal_nuit)
    assert res_norm_nuit.status_code == 200
    data_norm_nuit = res_norm_nuit.json()
    print(f"\n[TEST 3] Cas Normal Nuit : {payload_normal_nuit}")
    print(f"         Résultat -> est_anomalie={data_norm_nuit['est_anomalie']}, risque={data_norm_nuit['niveau_risque']}, score={data_norm_nuit['score_anomalie']}")
    assert data_norm_nuit["est_anomalie"] is False, "Le sommeil nocturne ne doit pas être considéré comme une anomalie."

    # Test 4 : ANOMALIE - Immobilité suspecte en plein jour (14h, distance = 0km, vitesse = 0km/h)
    payload_immobile_jour = {
        "animal_id": 103,
        "heure_jour": 14,
        "temperature_ambiante": 25.0,
        "humidite": 52.0,
        "qualite_air": 32.0,
        "distance_parcourue_km": 0.00,
        "vitesse_moyenne_kmh": 0.00,
    }
    res_immob = client.post("/predict", json=payload_immobile_jour)
    assert res_immob.status_code == 200
    data_immob = res_immob.json()
    print(f"\n[TEST 4] ANOMALIE Immobilité Diurne : {payload_immobile_jour}")
    print(f"         Résultat -> est_anomalie={data_immob['est_anomalie']}, risque={data_immob['niveau_risque']}, details='{data_immob['details']}'")
    assert data_immob["est_anomalie"] is True, "L'immobilité totale à 14h doit être détectée comme anomalie."

    # Test 5 : ANOMALIE - Fuite / vol nocturne (03h, distance = 4.5km, vitesse = 8.5km/h)
    payload_fuite_nuit = {
        "animal_id": 104,
        "heure_jour": 3,
        "temperature_ambiante": 12.0,
        "humidite": 75.0,
        "qualite_air": 26.0,
        "distance_parcourue_km": 4.5,
        "vitesse_moyenne_kmh": 8.5,
    }
    res_fuite = client.post("/predict", json=payload_fuite_nuit)
    assert res_fuite.status_code == 200
    data_fuite = res_fuite.json()
    print(f"\n[TEST 5] ANOMALIE Fuite/Vol Nocturne : {payload_fuite_nuit}")
    print(f"         Résultat -> est_anomalie={data_fuite['est_anomalie']}, risque={data_fuite['niveau_risque']}, details='{data_fuite['details']}'")
    assert data_fuite["est_anomalie"] is True, "Une course rapide à 3h du matin doit être détectée comme anomalie."
    assert data_fuite["niveau_risque"] == "CRITIQUE"

    # Test 6 : ANOMALIE - Stress thermique extrême et qualité d'air toxique (45°C, air = 260)
    payload_canicule = {
        "animal_id": 105,
        "heure_jour": 15,
        "temperature_ambiante": 45.5,
        "humidite": 90.0,
        "qualite_air": 260.0,
        "distance_parcourue_km": 0.05,
        "vitesse_moyenne_kmh": 0.06,
    }
    res_cani = client.post("/predict", json=payload_canicule)
    assert res_cani.status_code == 200
    data_cani = res_cani.json()
    print(f"\n[TEST 6] ANOMALIE Stress Thermique / Air Toxique : {payload_canicule}")
    print(f"         Résultat -> est_anomalie={data_cani['est_anomalie']}, risque={data_cani['niveau_risque']}, details='{data_cani['details']}'")
    assert data_cani["est_anomalie"] is True
    assert data_cani["niveau_risque"] == "CRITIQUE"

    print("\n" + "=" * 60)
    print(" [SUCCES] TOUS LES TESTS SONT PASSES AVEC SUCCES (6/6) !")
    print("=" * 60)


if __name__ == "__main__":
    test_suite()
