package com.esieeit.projetsi.infrastructure.repository;

import com.esieeit.projetsi.domain.entity.Project;
import com.esieeit.projetsi.domain.enumtype.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link Project} entities.
 */
@Repository
public interface ProjectJpaRepository extends JpaRepository<Project, Long> {

    // --- Text search on project name ---
    List<Project> findByNameContainingIgnoreCase(String keyword);

    // --- Query by owner ---
    List<Project> findByOwnerId(Long ownerId);

    // --- Query by status ---
    List<Project> findByStatus(ProjectStatus status);

    // --- Existence check (useful for avoiding duplicate project names per owner) ---
    boolean existsByName(String name);

    boolean existsByNameAndOwnerId(String name, Long ownerId);
}
