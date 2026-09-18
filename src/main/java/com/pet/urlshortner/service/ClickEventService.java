package com.pet.urlshortner.service;

import com.pet.urlshortner.dto.EventClickStatisticsDto;
import com.pet.urlshortner.exception.AppException;

import java.time.LocalDateTime;

public interface ClickEventService {
    EventClickStatisticsDto getClickStatistics(String shortCode, LocalDateTime from, LocalDateTime to);
}
