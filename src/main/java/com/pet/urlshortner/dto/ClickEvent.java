package com.pet.urlshortner.dto;

import jakarta.servlet.http.HttpServletRequest;

import java.time.Instant;
import java.util.UUID;

public record ClickEvent(
        String eventId,
        String shortCode,
        Instant timestamp,
        String ip,
        String userAgent,
        String referer
) {
    public static ClickEvent of(String shortCode, HttpServletRequest request) {
        return new ClickEvent(
                UUID.randomUUID().toString(),
                shortCode,
                Instant.now(),
                extractIp(request),
                request.getHeader("User-Agent"),
                request.getHeader("Referer")
        );
    }

    private static String extractIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        return (forwarded != null && !forwarded.isBlank())
                ? forwarded.split(",")[0].trim()
                : request.getRemoteAddr();
    }
}
