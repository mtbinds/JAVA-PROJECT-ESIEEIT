# Projet SI Java - ESIEE-IT (2025-2026)

## Contexte

Projet SI en Java : construire une API backend propre, structurée, documentée et testée, avec un workflow Git proche entreprise.

## Objectifs

- Mettre en place un dépôt Git propre (`main`/`develop`/`feature/*`)
- Implémenter un MVP (auth + gestion de ressources métier)
- Respecter une architecture claire (controller/service/repository)
- Ajouter des tests unitaires
- Produire une documentation exploitable (`README.md` + `BACKLOG.md`)

## Équipe

- Nom Prénom - rôle (PO / Lead Dev / Dev / QA)
- Nom Prénom - rôle
- Nom Prénom - rôle

## Stack

- Java 21
- Gradle (wrapper)
- JUnit 5
- (à venir) Spring Boot, DB, Docker

## Installation

### Prérequis

- Java 17/21
- Git

### Cloner

```bash
git clone <URL>
cd <repo>
```

## Lancer

### Tests

```bash
./gradlew test
```

### Run API Spring Boot

```bash
./gradlew bootRun
```

## Workflow Git

- `main` : stable
- `develop` : intégration
- `feature/*` : 1 user story = 1 branche
- PR obligatoire vers `develop`

## Convention de commits

- `chore(init): bootstrap gradle wrapper and project structure`
- `docs(readme): add setup and workflow instructions`
- `test(app): add initial sanity test`

Format recommandé : `<type>(<scope>): <message>`

Types courants : `feat`, `fix`, `chore`, `docs`, `refactor`, `test`.

## Backlog

Voir `BACKLOG.md`.

## Documentation de modélisation (TP 2.1)

- Modèle métier : `docs/DOMAIN_MODEL.md`
- Structure des packages : `docs/PACKAGE_STRUCTURE.md`
- Décisions d’architecture : `docs/DECISIONS.md`
- Règles métier et validations (TP 2.2) : `docs/DOMAIN_RULES.md`
- API REST Task CRUD (TP 3.1) : `docs/API_TASKS.md`

## API Tasks (TP 3.1)

Endpoints disponibles :

- `GET /api/tasks`
- `GET /api/tasks/{id}`
- `POST /api/tasks`
- `PUT /api/tasks/{id}`
- `DELETE /api/tasks/{id}`

Exemple rapide :

```bash
curl -i -X POST http://localhost:8080/api/tasks \
   -H "Content-Type: application/json" \
   -d '{"title":"Initialiser le repo","description":"Créer Gradle + README"}'
```

## Structure actuelle

```text
.
├─ build.gradle
├─ settings.gradle
├─ gradlew
├─ gradlew.bat
├─ gradle/wrapper/
└─ src/
   ├─ main/java/com/esieeit/projetsi/App.java
   └─ test/java/com/esieeit/projetsi/AppTest.java
```
