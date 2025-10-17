package com.backend.repository;

import com.backend.model.Progression;
import com.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProgressionRepository extends JpaRepository<Progression, Long> {

    /**
     * Récupère la progression associée à un utilisateur.
     */
    Optional<Progression> findByUser(User user);

    /**
     * Vérifie si une progression existe déjà pour cet utilisateur.
     */
    boolean existsByUser(User user);
}
