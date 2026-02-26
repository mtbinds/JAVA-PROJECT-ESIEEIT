# TP 4.2 – Rapport de synthèse

**Date :** 2026-02-26  
**Module :** Projet SI – ESIEE-IT  
**Séance :** 4 – Spring Data JPA, Repositories, Données de test

---

## 1. Repositories créés

Trois interfaces Spring Data JPA ont été créées dans `infrastructure/repository/` :

- **`TaskJpaRepository`** – accès aux tâches avec filtres par statut, projet, titre.
- **`ProjectJpaRepository`** – accès aux projets avec filtres par nom, propriétaire, statut.
- **`UserJpaRepository`** – accès aux utilisateurs avec recherche par email/username (préparation JWT).

## 2. Query methods ajoutées et pourquoi

| Méthode | Justification |
|---|---|
| `findByStatus(TaskStatus)` | Filtrage des tâches par statut (listing partiel) |
| `findByProjectId(Long)` | Toutes les tâches d'un projet donné |
| `findByProjectIdAndStatus(...)` | Filtrage combiné projet + statut |
| `findByTitleContainingIgnoreCase(String)` | Recherche textuelle sur le titre |
| `countByProjectId(Long)` | Tableau de bord : compte des tâches par projet |
| `existsByProjectIdAndTitleIgnoreCase(...)` | Contrôle de doublons dans le service |
| `findByEmail / findByUsername` | Pré-requis pour l'authentification JWT (Séance 5) |
| `existsByEmail / existsByUsername` | Validation d'unicité lors de la création d'utilisateur |
| `findByOwnerId` | Projets d'un utilisateur donné |
| `findByNameContainingIgnoreCase` | Recherche de projet par mot-clé |

## 3. Service migré

`TaskService` a été entièrement migré :
- **Suppression** du `InMemoryTaskRepository` (mocks en mémoire).
- **Injection** de `TaskJpaRepository` et `ProjectJpaRepository`.
- Création d'une tâche requiert désormais un `projectId` valide → le service vérifie l'existence du projet.
- Le contrôle de doublons (`existsByProjectIdAndTitleIgnoreCase`) est appliqué à la création.
- Les annotations `@Transactional` / `@Transactional(readOnly = true)` gèrent le contexte JPA.

## 4. Stratégie de seed

**`CommandLineRunner` via `DataInitializer`** (`@Profile("dev")`) :
- Avantage : logique Java, conditions de seed, relations complexes faciles à exprimer.
- **Idempotent** : le seed ne s'exécute que si les trois tables sont vides (`count() == 0`).
- Lancer avec : `./gradlew bootRun --args='--spring.profiles.active=dev'`

Jeu de données :
- 4 utilisateurs (admin, alice, bob, carol)
- 3 projets (statuts ACTIVE / DRAFT)
- 12 tâches réparties sur les 3 projets (statuts et priorités variés)

## 5. Problèmes rencontrés

- **Dual-model** : présence de deux hiérarchies (`domain.model.*` et `domain.entity.*`) héritées des TPs précédents. Résolu en faisant pointer `TaskService` directement sur les entités JPA et en mettant à jour `TaskMapper`/`TaskController`.
- **Méthodes de transition** : l'entité JPA n'expose pas toutes les méthodes du domain model (`archive()`, `moveBackToTodo()`). Résolu en utilisant le setter `setStatus()` dans le service pour les transitions simples.
- **Risque de `LazyInitializationException`** : géré par le mode `spring.jpa.open-in-view=true` (défaut Spring Boot) qui garde la session JPA ouverte tout au long de la requête HTTP.

## 6. Ce qui reste à améliorer avant la Séance 5 (JWT)

- Ajouter `UserService` avec création d'utilisateur (hashage du mot de passe avec BCrypt).
- Utiliser `UserJpaRepository.findByEmail()` dans le `UserDetailsService` de Spring Security.
- Introduire `ProjectController` et `ProjectService` complets.
- Envisager des DTOs séparés pour l'input/output des projets et des utilisateurs.
- Migrer vers Flyway pour versionner le schéma DB (`V1__init.sql`).
- Ajouter des tests d'intégration avec `@SpringBootTest` + base H2 in-memory.
