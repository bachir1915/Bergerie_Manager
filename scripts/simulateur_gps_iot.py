#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
=============================================================================
BERGERIE AL-BARAKA — Simulateur Matériel IoT (ESP32 + NEO-6M GPS + DHT22)
=============================================================================

Ce script simule fidèlement le comportement d'un réseau de capteurs ESP32
sans avoir besoin du matériel physique :
  - Émet des coordonnées GPS réalistes (mouvement brownien d'ovins en pâture)
  - Émet des mesures de température, humidité et qualité de l'air
  - Appelle les mêmes endpoints REST Spring Boot que le firmware C++ :
      * POST http://localhost:8085/api/iot/localisation
      * POST http://localhost:8085/api/iot/donnees-env

Utilisation :
  python scripts/simulateur_gps_iot.py [--interval 10] [--fugue]
=============================================================================
"""

import sys
import time
import random
import argparse
import urllib.request
import json

BASE_URL = "http://localhost:8085/api/iot"

# Données des animaux simulés (Bergerie Al-Baraka, Parcelles Assainies, Dakar, Sénégal)
ANIMAUX = [
    {"id": 1, "nom": "Thiouppe", "capteurId": 1, "lat": 14.7585, "lon": -17.4418},
    {"id": 2, "nom": "Ndeye",    "capteurId": 2, "lat": 14.7582, "lon": -17.4422},
    {"id": 3, "nom": "Samba",    "capteurId": 3, "lat": 14.7588, "lon": -17.4415},
    {"id": 4, "nom": "Aissa",    "capteurId": 4, "lat": 14.7578, "lon": -17.4425},
    {"id": 5, "nom": "Matar",    "capteurId": 5, "lat": 14.7584, "lon": -17.4410},
    {"id": 6, "nom": "Binta",    "capteurId": 6, "lat": 14.7575, "lon": -17.4418},
    {"id": 7, "nom": "Diouf",    "capteurId": 7, "lat": 14.7589, "lon": -17.4423},
    {"id": 8, "nom": "Khadija",  "capteurId": 8, "lat": 14.7580, "lon": -17.4412},
]

ZONES = [
    {"id": 1, "nom": "Enclos Parcelles Assainies", "temp": 27.5, "hum": 65.0, "air": 70.0},
    {"id": 2, "nom": "Paturage Unite 15",          "temp": 28.0, "hum": 62.0, "air": 72.0},
    {"id": 3, "nom": "Bercail Principal",          "temp": 26.5, "hum": 68.0, "air": 65.0},
]

def envoyer_post(endpoint, payload):
    url = f"{BASE_URL}/{endpoint}"
    data = json.dumps(payload).encode("utf-8")
    req = urllib.request.Request(url, data=data, headers={"Content-Type": "application/json"})
    try:
        with urllib.request.urlopen(req, timeout=5) as response:
            return response.status, json.loads(response.read().decode("utf-8"))
    except Exception as e:
        return 0, str(e)

def simuler_gps(animal, fugue=False):
    if fugue:
        # Déplacement brutal hors de la zone (+500m)
        animal["lat"] += (random.choice([-1, 1])) * 0.0050
        animal["lon"] += (random.choice([-1, 1])) * 0.0050
        vitesse = round(random.uniform(5.5, 8.5), 1)
    else:
        # Déplacement doux de pâturage (5-10m)
        delta_lat = random.gauss(0, 0.00008)
        delta_lon = random.gauss(0, 0.00008)
        animal["lat"] += delta_lat
        animal["lon"] += delta_lon
        vitesse = round(random.uniform(0.4, 2.2), 1)

    payload = {
        "animalId": animal["id"],
        "capteurId": animal["capteurId"],
        "latitude": round(animal["lat"], 6),
        "longitude": round(animal["lon"], 6),
        "vitesse": vitesse,
        "precisionGps": round(random.uniform(1.5, 3.5), 1)
    }

    status, resp = envoyer_post("localisation", payload)
    if status == 200:
        print(f"  [GPS ESP32 #{animal['id']}] 🐑 {animal['nom']:<9} -> Lat: {payload['latitude']:.5f}, Lon: {payload['longitude']:.5f} ({vitesse} km/h) [OK]")
    else:
        print(f"  [GPS ESP32 #{animal['id']}] ❌ Erreur: {resp}")

def simuler_climat(zone):
    zone["temp"] += random.gauss(0, 0.3)
    zone["hum"] += random.gauss(0, 0.6)
    zone["air"] += random.gauss(0, 1.2)

    payload = {
        "zoneId": zone["id"],
        "capteurId": zone["id"],
        "temperature": round(zone["temp"], 1),
        "humidite": round(zone["hum"], 1),
        "qualiteAir": round(zone["air"], 1)
    }

    status, resp = envoyer_post("donnees-env", payload)
    if status == 200:
        print(f"  [ENV DHT22 #{zone['id']}] 🌡️  {zone['nom']:<20} -> {payload['temperature']}°C | {payload['humidity'] if 'humidity' in payload else payload['humidite']}% | Air: {payload['qualiteAir']} [OK]")
    else:
        print(f"  [ENV DHT22 #{zone['id']}] ❌ Erreur: {resp}")

def main():
    parser = argparse.ArgumentParser(description="Simulateur de balises GPS IoT et capteurs ESP32 pour la Bergerie")
    parser.add_argument("--interval", type=int, default=10, help="Intervalle entre chaque envoi en secondes (défaut: 10s)")
    parser.add_argument("--once", action="store_true", help="N'exécuter qu'un seul cycle et quitter")
    parser.add_argument("--fugue", action="store_true", help="Simuler immédiatement une fugue de bête hors du périmètre")
    args = parser.parse_args()

    print("=" * 70)
    print("📡 SIMULATEUR MATÉRIEL IoT (ESP32 + NEO-6M + DHT22)")
    print(f"   Cible : {BASE_URL}")
    print(f"   Animaux simulés : {len(ANIMAUX)} colliers connectés")
    print(f"   Zones ambiantes  : {len(ZONES)} modules DHT22/MQ-135")
    print("=" * 70)

    if args.fugue:
        fugitif = random.choice(ANIMAUX)
        print(f"\n🚨 [SCÉNARIO GÉOFENCING] Simulation de fugue pour l'animal '{fugitif['nom']}'...")
        simuler_gps(fugitif, fugue=True)
        print("Alerte envoyée au backend Spring Boot. Vérifiez la carte et les alertes !")
        return

    tick = 1
    try:
        while True:
            print(f"\n--- Cycle #{tick} [{time.strftime('%H:%M:%S')}] ---")
            print(">> Transmission GPS colliers :")
            for a in ANIMAUX:
                simuler_gps(a)

            if tick % 2 == 1:
                print(">> Transmission capteurs d'ambiance DHT22 :")
                for z in ZONES:
                    simuler_climat(z)

            if args.once:
                break

            print(f"Attente de {args.interval}s avant le prochain cycle (Ctrl+C pour stopper)...")
            time.sleep(args.interval)
            tick += 1

    except KeyboardInterrupt:
        print("\nArrêt du simulateur IoT.")

if __name__ == "__main__":
    main()
