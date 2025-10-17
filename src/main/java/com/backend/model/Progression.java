package com.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "progressions")
public class Progression {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 Chaque progression appartient à un utilisateur
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 📘 Documents en cours
    @ManyToMany
    @JoinTable(
            name = "progression_in_progress",
            joinColumns = @JoinColumn(name = "progression_id"),
            inverseJoinColumns = @JoinColumn(name = "document_id")
    )
    @Builder.Default
    private List<Document> inProgress = new ArrayList<>();

    // ✅ Documents terminés
    @ManyToMany
    @JoinTable(
            name = "progression_complete",
            joinColumns = @JoinColumn(name = "progression_id"),
            inverseJoinColumns = @JoinColumn(name = "document_id")
    )
    @Builder.Default
    private List<Document> complete = new ArrayList<>();

    // ⭐ Documents favoris
    @ManyToMany
    @JoinTable(
            name = "progression_favorites",
            joinColumns = @JoinColumn(name = "progression_id"),
            inverseJoinColumns = @JoinColumn(name = "document_id")
    )
    @Builder.Default
    private List<Document> favorites = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
