package com.pet.urlshortner.controller;

import com.pet.urlshortner.dto.EventClickStatisticsDto;
import com.pet.urlshortner.service.ClickEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/stat")
@RequiredArgsConstructor
@Validated
public class StatController {
    private final ClickEventService clickEventService;

    @GetMapping("/{shortCode}")
    public EventClickStatisticsDto getBaseStatistics(
            @PathVariable String shortCode,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return clickEventService.getClickStatistics(shortCode, from, to);
    }
}
