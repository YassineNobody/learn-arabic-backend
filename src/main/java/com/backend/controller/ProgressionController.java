package com.backend.controller;

import com.backend.dto.common.SuccessResponse;
import com.backend.dto.progression.ProgressionResponse;
import com.backend.service.ProgressionService;
import com.backend.util.ResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/progressions")
@RequiredArgsConstructor
public class ProgressionController {

    private final ProgressionService progressionService;

    /**
     * 📥 Récupère la progression de l’utilisateur connecté.
     */
    @GetMapping("/me")
    public ResponseEntity<SuccessResponse<ProgressionResponse>> getCurrentProgression() {
        var progression = progressionService.getProgression();
        return ResponseEntity.ok(ResponseFactory.success(progression));
    }

    /**
     * 🔁 Ajoute ou met à jour la progression d’un document.
     *
     * Exemple :
     * PUT /api/progressions?progress=document-slug
     * PUT /api/progressions?complete=document-slug
     * PUT /api/progressions?favorite=document-slug
     */
    @PutMapping
    public ResponseEntity<SuccessResponse<ProgressionResponse>> updateProgression(
            @RequestParam(required = false) String progress,
            @RequestParam(required = false) String complete,
            @RequestParam(required = false) String favorite
    ) {
        var progression = progressionService.updateProgression(progress, complete, favorite);
        return ResponseEntity.ok(ResponseFactory.success(progression));
    }

    /**
     * ❌ Supprime un document d’une liste (progress/complete/favorite).
     *
     * Exemple :
     * DELETE /api/progressions?favorite=document-slug
     */
    @DeleteMapping
    public ResponseEntity<SuccessResponse<ProgressionResponse>> deleteProgression(
            @RequestParam(required = false) String progress,
            @RequestParam(required = false) String complete,
            @RequestParam(required = false) String favorite
    ) {
        var progression = progressionService.deleteProgression(progress, complete, favorite);
        return ResponseEntity.ok(ResponseFactory.success(progression));
    }
}
