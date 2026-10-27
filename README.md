# Bergerie Manager — plateforme IoT de surveillance des troupeaux

| Service | Techno | Port |
|---|---|---|
| backend | Spring Boot 3.2 / Java 17 | 8085 |
| ml | FastAPI + Isolation Forest | 8001 |
| frontend | Angular 21 (nginx) | 4200 |
| db | PostgreSQL 16 | interne |

## Lancer toute la stack
```
docker compose up -d --build
```
Frontend : http://localhost:4200 — API/Swagger : http://localhost:8085/swagger-ui.html — ML : http://localhost:8001/health

## Tests
- ML : `cd ml-service && python train_model.py && python test_client.py`
- Backend : `mvn test` (JDK 17 recommandé)

## CI/CD
GitHub Actions (`.github/workflows/ci.yml`), GitLab CI (`.gitlab-ci.yml`), Jenkins (`Jenkinsfile`) :
tests backend + ML, build frontend, build des 3 images Docker.
Jenkins : créer l'identifiant `dockerhub` (Username/Password) pour l'étape Push.
