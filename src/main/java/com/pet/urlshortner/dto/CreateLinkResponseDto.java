package com.pet.urlshortner.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CreateLinkResponseDto(
        Long id,
        String shortUrl,
        String originalUrl,
        String redirectType,
        LocalDateTime createdAt,
        LocalDateTime expiresAt,
        boolean isActive
) {
}
