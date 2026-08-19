package com.pet.urlshortner.config.shortenerConfig;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "shortener")
public record ShortenerConfigProperties(
        int shortCodeLength,
        int shortLinkLifeTimeDays,
        String baseUrl
) {
}

