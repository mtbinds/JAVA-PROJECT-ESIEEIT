package com.esieeit.projetsi.infrastructure.seed;

import com.esieeit.projetsi.domain.entity.Project;
import com.esieeit.projetsi.domain.entity.Task;
import com.esieeit.projetsi.domain.entity.User;
import com.esieeit.projetsi.domain.enumtype.ProjectStatus;
import com.esieeit.projetsi.domain.enumtype.Role;
import com.esieeit.projetsi.domain.enumtype.TaskPriority;
import com.esieeit.projetsi.domain.enumtype.TaskStatus;
import com.esieeit.projetsi.infrastructure.repository.ProjectJpaRepository;
import com.esieeit.projetsi.infrastructure.repository.TaskJpaRepository;
import com.esieeit.projetsi.infrastructure.repository.UserJpaRepository;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Seeds the database with demo data on startup (only in "dev" profile and only
 * if the tables are empty – idempotent).
 *
 * Run with: ./gradlew bootRun --args='--spring.profiles.active=dev'
 */
@Configuration
@Profile("dev")
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserJpaRepository userRepository;
    private final ProjectJpaRepository projectRepository;
    private final TaskJpaRepository taskRepository;

    public DataInitializer(UserJpaRepository userRepository,
                           ProjectJpaRepository projectRepository,
                           TaskJpaRepository taskRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    @Bean
    CommandLineRunner initData() {
        return args -> {

            // Idempotence guard – skip if data already exists
            if (userRepository.count() > 0 || projectRepository.count() > 0 || taskRepository.count() > 0) {
                log.info("[DataInitializer] Data already present – seed skipped.");
                return;
            }

            log.info("[DataInitializer] Seeding demo data...");

            // ------------------------------------------------------------------
            // 1) Users
            // ------------------------------------------------------------------
            User admin = new User("admin", "admin@esiee.local", "CHANGE_ME_HASH_LATER", Role.ROLE_ADMIN);
            User alice = new User("alice", "alice@esiee.local", "CHANGE_ME_HASH_LATER", Role.ROLE_USER);
            User bob   = new User("bob",   "bob@esiee.local",   "CHANGE_ME_HASH_LATER", Role.ROLE_USER);
            User carol = new User("carol", "carol@esiee.local", "CHANGE_ME_HASH_LATER", Role.ROLE_USER);

            userRepository.saveAll(List.of(admin, alice, bob, carol));

            // ------------------------------------------------------------------
            // 2) Projects
            // ------------------------------------------------------------------
            Project p1 = new Project("Projet SI - Gestion des tâches",
                    "Plateforme de gestion de projets et tâches pour le TP ESIEE-IT",
                    ProjectStatus.ACTIVE,
                    admin);

            Project p2 = new Project("Projet SI - Support interne",
                    "Gestion des tickets de support utilisateur",
                    ProjectStatus.ACTIVE,
                    alice);

            Project p3 = new Project("Projet SI - Refonte API",
                    "Modernisation des APIs REST existantes",
                    ProjectStatus.DRAFT,
                    bob);

            projectRepository.saveAll(List.of(p1, p2, p3));

            // ------------------------------------------------------------------
            // 3) Tasks (liées aux projets)
            // ------------------------------------------------------------------
            LocalDate today = LocalDate.now();

            // -- Project 1 tasks --
            Task t1 = task("Initialiser le repository Git",
                    "Créer le dépôt, les branches et les conventions de commit",
                    TaskStatus.DONE, TaskPriority.HIGH, today.minusDays(10), p1);

            Task t2 = task("Créer les entités JPA",
                    "Mapper User, Project, Task et Comment avec Hibernate",
                    TaskStatus.DONE, TaskPriority.HIGH, today.minusDays(3), p1);

            Task t3 = task("Créer les repositories Spring Data",
                    "TaskJpaRepository, ProjectJpaRepository, UserJpaRepository",
                    TaskStatus.IN_PROGRESS, TaskPriority.HIGH, today.plusDays(1), p1);

            Task t4 = task("Migrer TaskService vers JPA",
                    "Remplacer InMemoryTaskRepository par les repositories JPA",
                    TaskStatus.IN_PROGRESS, TaskPriority.HIGH, today.plusDays(2), p1);

            Task t5 = task("Ajouter le jeu de données de démonstration",
                    "CommandLineRunner avec DataInitializer",
                    TaskStatus.TODO, TaskPriority.MEDIUM, today.plusDays(2), p1);

            Task t6 = task("Documenter les endpoints dans le README",
                    "Exemples curl pour GET, POST, PUT, DELETE",
                    TaskStatus.TODO, TaskPriority.LOW, today.plusDays(5), p1);

            // -- Project 2 tasks --
            Task t7 = task("Analyser les demandes de support en attente",
                    "Triage des tickets entrants",
                    TaskStatus.TODO, TaskPriority.URGENT, today.plusDays(1), p2);

            Task t8 = task("Mettre en place le processus d'escalade",
                    "Définir les niveaux de priorité",
                    TaskStatus.IN_PROGRESS, TaskPriority.MEDIUM, today.plusDays(4), p2);

            Task t9 = task("Rédiger la FAQ interne",
                    "Questions fréquentes des utilisateurs",
                    TaskStatus.TODO, TaskPriority.LOW, today.plusDays(7), p2);

            // -- Project 3 tasks --
            Task t10 = task("Audit des APIs existantes",
                    "Lister les endpoints à migrer",
                    TaskStatus.TODO, TaskPriority.HIGH, today.plusDays(5), p3);

            Task t11 = task("Ajouter la validation Bean Validation",
                    "Annoter les DTOs avec les contraintes Jakarta",
                    TaskStatus.TODO, TaskPriority.MEDIUM, today.plusDays(7), p3);

            Task t12 = task("Implémenter la sécurité JWT",
                    "Spring Security + tokens JWT pour l'authentification",
                    TaskStatus.TODO, TaskPriority.HIGH, today.plusDays(14), p3);

            taskRepository.saveAll(List.of(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12));

            log.info("[DataInitializer] Seed complete: {} users, {} projects, {} tasks.",
                    userRepository.count(), projectRepository.count(), taskRepository.count());
        };
    }

    // Helper to build a Task without repetition
    private Task task(String title, String description, TaskStatus status,
                      TaskPriority priority, LocalDate dueDate, Project project) {
        Task t = new Task();
        t.setTitle(title);
        t.setDescription(description);
        t.setStatus(status);
        t.setPriority(priority);
        t.setDueDate(dueDate);
        t.setProject(project);
        return t;
    }
}
