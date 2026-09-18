package com.pet.urlshortner.dto;

import java.time.LocalDateTime;

public record EventClickStatisticsDto(
        String ShortCode,
        Long totalClicks,
        Long uniqueClick,
        LocalDateTime firstClickAt,
        LocalDateTime lastClickAt
) {
}
