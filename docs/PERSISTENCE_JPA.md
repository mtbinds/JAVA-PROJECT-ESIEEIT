# Persistance JPA / Hibernate (TP 4.1)

## Objectif

Mettre en place une couche de persistance relationnelle avec Spring Data JPA + Hibernate sur MySQL.

## Packages ajoutés

- `com.esieeit.projetsi.domain.enumtype`
  - `Role`
  - `ProjectStatus`
  - `TaskStatus`
  - `TaskPriority`
- `com.esieeit.projetsi.domain.entity`
  - `User`
  - `Project`
  - `Task`
  - `Comment`

## Modélisation relationnelle

- `User (1) -> (N) Project` via `Project.owner`
- `Project (1) -> (N) Task` via `Task.project`
- `User (1) -> (N) Task` via `Task.assignee` (optionnel)
- `Task (1) -> (N) Comment` via `Comment.task`
- `User (1) -> (N) Comment` via `Comment.author`

## Choix techniques

- `@Enumerated(EnumType.STRING)` pour préserver la lisibilité et la stabilité des enums en base.
- `FetchType.LAZY` sur les associations pour éviter les chargements inutiles.
- `cascade = CascadeType.ALL` + `orphanRemoval = true` sur `Project.tasks` et `Task.comments`.
- Timestamps automatiques avec `@PrePersist` / `@PreUpdate` (`createdAt`, `updatedAt`).
- Contraintes d’unicité sur `users.email` et `users.username`.

## Configuration runtime

- Fichier Spring : `src/main/resources/application.yml`
- Variables d’environnement gérées via `.env` (base `.env.example`)
- Infra locale : `docker-compose.yml` (MySQL + phpMyAdmin)

## Points d'attention

- Les classes métier historiques dans `domain.model` sont conservées (compatibilité TP 2.x / 3.x).
- Les nouvelles entités JPA sont isolées dans `domain.entity` pour préparer la migration progressive vers un repository SQL.