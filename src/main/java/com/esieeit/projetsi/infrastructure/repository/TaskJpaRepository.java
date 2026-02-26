package com.esieeit.projetsi.infrastructure.repository;

import com.esieeit.projetsi.domain.entity.Task;
import com.esieeit.projetsi.domain.enumtype.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link Task} entities.
 * Provides CRUD operations and domain-specific query methods.
 */
@Repository
public interface TaskJpaRepository extends JpaRepository<Task, Long> {

    // --- Query methods by status ---
    List<Task> findByStatus(TaskStatus status);

    // --- Query methods by project ---
    List<Task> findByProjectId(Long projectId);

    List<Task> findByProjectIdAndStatus(Long projectId, TaskStatus status);

    List<Task> findByProjectIdOrderByCreatedAtDesc(Long projectId);

    // --- Text search ---
    List<Task> findByTitleContainingIgnoreCase(String keyword);

    // --- Counting ---
    long countByProjectId(Long projectId);

    // --- Existence checks ---
    boolean existsByProjectIdAndTitleIgnoreCase(Long projectId, String title);
}
