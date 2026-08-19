package com.pet.urlshortner.entity;

import com.pet.urlshortner.enums.RedirectType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "links")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Link {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "short_code", nullable = false, unique = true, length = 30)
    private String shortCode;

    @Column(name = "original_url", nullable = false, length = 2048)
    private String originalUrl;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "redirect_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private RedirectType redirectType;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;
}
