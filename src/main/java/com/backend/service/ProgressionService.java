package com.backend.service;

import com.backend.dto.progression.ProgressionResponse;
import com.backend.mapper.ProgressionMapper;
import com.backend.model.Document;
import com.backend.model.Progression;
import com.backend.model.User;
import com.backend.repository.DocumentRepository;
import com.backend.repository.ProgressionRepository;
import com.backend.util.SecurityUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgressionService {

    private final ProgressionRepository progressionRepository;
    private final DocumentRepository documentRepository;

    // 🔎 Trouver un document par slug
    private Document findDocument(String slug) {
        return documentRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Document introuvable"));
    }

    // 📊 Récupérer ou créer une progression
    private Progression getOrCreateProgression(User user) {
        return progressionRepository.findByUser(user)
                .orElseGet(() -> progressionRepository.save(
                        Progression.builder().user(user).build()
                ));
    }

    // 🔁 Vérifie si un document est déjà dans une liste
    private boolean isInList(List<Document> documents, Document document) {
        return documents.contains(document);
    }

    /**
     * ➕ Ajoute un document dans la liste spécifiée, en retirant des autres si nécessaire.
     */
    @Transactional
    private ProgressionResponse addToList(User user, String slug, String type) {
        var document = findDocument(slug);
        var progression = getOrCreateProgression(user);

        switch (type.toLowerCase()) {
            case "progress" -> {
                progression.getComplete().remove(document);
                if (!isInList(progression.getInProgress(), document)) {
                    progression.getInProgress().add(document);
                }
            }
            case "complete" -> {
                progression.getInProgress().remove(document);
                if (!isInList(progression.getComplete(), document)) {
                    progression.getComplete().add(document);
                }
            }
            case "favorite" -> {
                if (!isInList(progression.getFavorites(), document)) {
                    progression.getFavorites().add(document);
                }
            }
            default -> throw new IllegalStateException("Type de progression inconnu : " + type);
        }

        var saved = progressionRepository.save(progression);
        return ProgressionMapper.toResponse(saved);
    }

    /**
     * ➖ Supprime un document d’une des listes.
     */
    @Transactional
    private ProgressionResponse removeFromList(User user, String slug, String type) {
        var document = findDocument(slug);
        var progression = getOrCreateProgression(user);

        switch (type.toLowerCase()) {
            case "progress" -> progression.getInProgress().remove(document);
            case "complete" -> progression.getComplete().remove(document);
            case "favorite" -> progression.getFavorites().remove(document);
            default -> throw new IllegalStateException("Type de progression inconnu : " + type);
        }

        var saved = progressionRepository.save(progression);
        return ProgressionMapper.toResponse(saved);
    }

    /**
     * 🚀 Initialisation automatique de la progression après vérification du mail.
     */
    @Transactional
    public ProgressionResponse initProgression(User user) {
        return progressionRepository.findByUser(user)
                .map(ProgressionMapper::toResponse)
                .orElseGet(() -> {
                    var newProgression = progressionRepository.save(
                            Progression.builder().user(user).build()
                    );
                    return ProgressionMapper.toResponse(newProgression);
                });
    }

    /**
     * 🔁 Mise à jour de la progression selon le type d’ajout.
     */
    @Transactional
    public ProgressionResponse updateProgression(String progress, String complete, String favorites) {
        User user = SecurityUtil.getCurrentUserOrNull();
        if (user == null) {
            return null;
        }

        if (progress != null) {
            return addToList(user, progress, "progress");
        } else if (complete != null) {
            return addToList(user, complete, "complete");
        } else if (favorites != null) {
            return addToList(user, favorites, "favorite");
        }

        throw new IllegalStateException("Aucune action d’ajout spécifiée");
    }

    /**
     * ❌ Suppression d’une progression spécifique (retirer un document d’une liste).
     */
    @Transactional
    public ProgressionResponse deleteProgression(String progress, String complete, String favorites) {
        User user = SecurityUtil.getCurrentUserOrNull();
        if (user == null) {
            return null;
        }

        if (progress != null) {
            return removeFromList(user, progress, "progress");
        } else if (complete != null) {
            return removeFromList(user, complete, "complete");
        } else if (favorites != null) {
            return removeFromList(user, favorites, "favorite");
        }

        throw new IllegalStateException("Aucune action de suppression spécifiée");
    }

    /**
     * 📥 Récupère la progression de l’utilisateur connecté.
     */
    @Transactional
    public ProgressionResponse getProgression() {
        User user = SecurityUtil.getCurrentUserOrNull();
        if (user == null) {
            return null;
        }
        return ProgressionMapper.toResponse(getOrCreateProgression(user));
    }
}
