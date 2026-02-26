package com.esieeit.projetsi.infrastructure.repository;

import com.esieeit.projetsi.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link User} entities.
 * Includes query methods useful for future JWT authentication (Séance 5).
 */
@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {

    // --- Authentication queries (JWT - Séance 5) ---
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    // --- Existence checks ---
    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
