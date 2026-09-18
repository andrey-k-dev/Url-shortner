package com.pet.urlshortner.dto;

import com.pet.urlshortner.enums.RedirectType;

import java.time.LocalDateTime;

public record LinkCache(
        String shortCode,
        String originalUrl,
        RedirectType redirectType,
        boolean active,
        LocalDateTime expiredAt
) {
}