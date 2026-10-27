# Compléments à intégrer au mémoire

## 0. Corrections du PDF actuel
- Page 2 (Résumé) : balises `<br/><br/>` affichées en brut -> les remplacer par un saut de paragraphe.
- Page 6 : `<b>Isolation Forest</b>` affiché en brut -> retirer les balises.
- Page 1 : compléter le jury et l'année académique ; page 2 : vraie dédicace/remerciements.
- Page 6, tableau des niveaux d'alerte : les seuils 0.5 / 0.65 / 0.8 ne correspondent PAS au code.
  Le code (`ml-service/app/main.py`) applique des règles métier puis des seuils de score :
  score >= 0.65 CRITIQUE, >= 0.56 ELEVE, sinon MODERE (si `predict()` = -1) ; NORMAL si inlier.
  Règles prioritaires : temp >= 40 °C ou air >= 180 -> CRITIQUE ; nuit (h<6 ou h>=21) et distance >= 1 km -> CRITIQUE ;
  immobilité diurne (7-18 h, dist et vitesse <= 0.05) -> ELEVE.
  => Remplacer le tableau par celui-ci, ou changer le code.
- Section 2 (Architecture) : indiquer que les capteurs sont simulés (`scripts/simulateur_gps_iot.py`, `IotSimulationService`) si le matériel n'a pas été testé.
- Le mémoire ne parle pas de : authentification Spring Security + CORS, Swagger, migrations SQL, profils dev (H2) / prod (PostgreSQL), scheduler horaire (`ml.analysis.cron`), frontend Angular, notifications. À ajouter en Partie 3.

## 1. Partie 3 — Chapitre 4 : Intégration et déploiement continus (DevOps)
**Conteneurisation (Docker).** Chaque composant a son Dockerfile (build multi-étapes pour Spring Boot et Angular). `docker-compose.yml` orchestre PostgreSQL, le service ML, le backend et le frontend ; le backend n'attend que la base et le ML soient « healthy ».

**Pipeline CI/CD.** Étapes : checkout -> tests (backend Maven, ML pytest) en parallèle -> build Angular -> build des images -> push registre -> déploiement `docker compose up -d`.
Le même pipeline est décrit pour trois outils : GitHub Actions, GitLab CI, Jenkins.

Captures à insérer (une par ligne) :
- Fig. X : dépôt GitHub avec les fichiers du projet
- Fig. X : dépôt GitLab (miroir) + pipeline vert
- Fig. X : GitHub Actions, run vert
- Fig. X : Jenkins, Stage View vert
- Fig. X : `docker compose ps` (4 services Up)
- Fig. X : application dans le navigateur (login, dashboard, carte, alertes)

## 2. Partie 3 — Résultats des tests
Tests ML (`ml-service/test_client.py`) : 6/6 réussis (santé, jour normal, nuit normale, immobilité diurne, fuite nocturne, stress thermique).
Tests backend : `AlerteServiceTest`, `AnimalServiceTest`, `AnomalieDetectionServiceTest`.

## 3. Bibliographie (à compléter)
ETSI EN 303 645 ; Liu, Ting, Zhou (2008) « Isolation Forest », IEEE ICDM ; documentation Spring Boot, FastAPI, scikit-learn, Angular, Docker.
