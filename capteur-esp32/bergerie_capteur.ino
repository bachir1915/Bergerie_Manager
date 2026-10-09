/*
 * ============================================================
 *  BERGERIE AL-BARAKA — Firmware ESP32
 *  Capteur Connecté IoT : GPS + DHT22 + MQ-135 + SIM800L
 * ============================================================
 *
 *  MATÉRIEL :
 *    - ESP32           → Microcontrôleur principal Wi-Fi/BLE
 *    - DHT22 (GPIO 4)  → Température & Humidité ambiante
 *    - MQ-135 (GPIO 34)→ Qualité de l'air (NH₃, CO₂...)
 *    - NEO-6M (Serial2)→ GPS latitude, longitude, vitesse
 *    - SIM800L         → GSM (secours sans Wi-Fi)
 *
 *  ENDPOINTS SPRING BOOT (port 8085) :
 *    POST /api/iot/localisation   → Données GPS animal
 *    POST /api/iot/donnees-env    → Données environnementales zone
 *
 *  BIBLIOTHÈQUES (Arduino IDE / PlatformIO) :
 *    - DHT sensor library (Adafruit)
 *    - Adafruit Unified Sensor
 *    - TinyGPSPlus (Mikal Hart)
 *    - ArduinoJson >= v6 (Benoit Blanchon)
 *    - HTTPClient (inclus ESP32 Arduino Core)
 *
 *  WIRING :
 *    DHT22     DATA  → GPIO 4
 *    MQ-135    AOUT  → GPIO 34
 *    GPS       TX    → GPIO 16 (RX de Serial2)
 *    GPS       RX    → GPIO 17 (TX de Serial2)
 * ============================================================
 */

#include <Arduino.h>
#include <WiFi.h>
#include <HTTPClient.h>
#include <ArduinoJson.h>
#include <DHT.h>
#include <TinyGPSPlus.h>
#include <HardwareSerial.h>

// ============================
//   CONFIGURATION À MODIFIER
// ============================
const char* WIFI_SSID     = "Votre_SSID_WiFi";
const char* WIFI_PASSWORD = "Votre_Mot_De_Passe_WiFi";
const char* API_BASE      = "http://192.168.1.100:8085/api/iot";

// Identifiants base de données
const long ANIMAL_ID  = 1;
const long CAPTEUR_ID = 1;
const long ZONE_ID    = 1;

// Intervalles d'envoi
const unsigned long INTERVALLE_GPS_MS = 30000;  // 30 secondes
const unsigned long INTERVALLE_ENV_MS = 60000;  // 60 secondes

// ============================
//   BROCHAGES
// ============================
#define DHT_PIN   4
#define DHT_TYPE  DHT22
#define MQ135_PIN 34

// ============================
//   OBJETS GLOBAUX
// ============================
DHT dht(DHT_PIN, DHT_TYPE);
TinyGPSPlus gps;
HardwareSerial gpsSerial(2);  // Serial2

unsigned long dernierEnvoiGps = 0;
unsigned long dernierEnvoiEnv = 0;

// ============================================================
//   SETUP
// ============================================================
void setup() {
  Serial.begin(115200);
  delay(500);
  Serial.println("\n=== Bergerie Al-Baraka — ESP32 IoT ===");

  dht.begin();
  gpsSerial.begin(9600, SERIAL_8N1, 16, 17);  // RX=16, TX=17
  connecterWiFi();
}

// ============================================================
//   BOUCLE PRINCIPALE
// ============================================================
void loop() {
  // Alimentation continue du parseur GPS
  while (gpsSerial.available() > 0) {
    gps.encode(gpsSerial.read());
  }

  unsigned long maintenant = millis();

  if (maintenant - dernierEnvoiGps >= INTERVALLE_GPS_MS) {
    dernierEnvoiGps = maintenant;
    envoyerDonneesGPS();
  }

  if (maintenant - dernierEnvoiEnv >= INTERVALLE_ENV_MS) {
    dernierEnvoiEnv = maintenant;
    envoyerDonneesEnvironnement();
  }

  if (WiFi.status() != WL_CONNECTED) {
    Serial.println("[WiFi] Reconnexion...");
    connecterWiFi();
  }
}

// ============================================================
//   CONNEXION WI-FI
// ============================================================
void connecterWiFi() {
  Serial.print("[WiFi] Connexion à : ");
  Serial.println(WIFI_SSID);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);

  int t = 0;
  while (WiFi.status() != WL_CONNECTED && t < 20) {
    delay(500);
    Serial.print(".");
    t++;
  }

  if (WiFi.status() == WL_CONNECTED) {
    Serial.println("\n[WiFi] OK — IP : " + WiFi.localIP().toString());
  } else {
    Serial.println("\n[WiFi] ÉCHEC — Prochaine tentative dans 30s");
  }
}

// ============================================================
//   LECTURE MQ-135 → AQI simplifié (0–500)
// ============================================================
float lireQualiteAir() {
  int brut = analogRead(MQ135_PIN);  // 0-4095 (ADC 12 bits)
  float aqi = map(brut, 0, 4095, 0, 500);
  Serial.println("[MQ-135] ADC=" + String(brut) + " AQI~" + String(aqi));
  return aqi;
}

// ============================================================
//   ENVOI GPS → POST /api/iot/localisation
// ============================================================
void envoyerDonneesGPS() {
  if (WiFi.status() != WL_CONNECTED) return;

  double lat = 0.0, lon = 0.0, vitesse = 0.0, precision = 99.9;

  if (gps.location.isValid() && gps.location.age() < 5000) {
    lat       = gps.location.lat();
    lon       = gps.location.lng();
    vitesse   = gps.speed.kmph();
    precision = gps.hdop.hdop();
    Serial.println("[GPS] Fix : " + String(lat, 6) + ", " + String(lon, 6) +
                   "  v=" + String(vitesse, 1) + " km/h");
  } else {
    Serial.println("[GPS] Pas de fix — attente satellites...");
  }

  StaticJsonDocument<256> doc;
  doc["animalId"]    = ANIMAL_ID;
  doc["capteurId"]   = CAPTEUR_ID;
  doc["latitude"]    = lat;
  doc["longitude"]   = lon;
  doc["vitesse"]     = vitesse;
  doc["precisionGps"] = precision;

  String payload;
  serializeJson(doc, payload);
  envoyerHTTP(String(API_BASE) + "/localisation", payload, "GPS");
}

// ============================================================
//   ENVOI DONNÉES ENV → POST /api/iot/donnees-env
// ============================================================
void envoyerDonneesEnvironnement() {
  if (WiFi.status() != WL_CONNECTED) return;

  float temperature = dht.readTemperature();
  float humidite    = dht.readHumidity();

  if (isnan(temperature) || isnan(humidite)) {
    Serial.println("[DHT22] ERREUR lecture — vérifiez le câblage GPIO " + String(DHT_PIN));
    return;
  }

  float qualiteAir = lireQualiteAir();

  Serial.println("[DHT22] Temp=" + String(temperature, 1) + "°C  Hum=" + String(humidite, 1) + "%");

  StaticJsonDocument<256> doc;
  doc["zoneId"]      = ZONE_ID;
  doc["capteurId"]   = CAPTEUR_ID;
  doc["temperature"] = temperature;
  doc["humidite"]    = humidite;
  doc["qualiteAir"]  = qualiteAir;

  String payload;
  serializeJson(doc, payload);
  envoyerHTTP(String(API_BASE) + "/donnees-env", payload, "ENV");
}

// ============================================================
//   HTTP POST GÉNÉRIQUE
// ============================================================
void envoyerHTTP(String url, String payload, String tag) {
  HTTPClient http;
  http.begin(url);
  http.addHeader("Content-Type", "application/json");
  http.setTimeout(8000);

  Serial.println("[" + tag + "] POST → " + url);

  int code = http.POST(payload);
  if (code > 0) {
    Serial.println("[" + tag + "] HTTP " + String(code) + " : " + http.getString());
  } else {
    Serial.println("[" + tag + "] ERREUR : " + http.errorToString(code));
  }
  http.end();
}
