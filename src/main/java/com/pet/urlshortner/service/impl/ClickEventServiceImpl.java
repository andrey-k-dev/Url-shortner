package com.pet.urlshortner.service.impl;

import com.pet.urlshortner.dto.EventClickStatisticsDto;
import com.pet.urlshortner.exception.AppException;
import com.pet.urlshortner.repository.ClickEventRepository;
import com.pet.urlshortner.service.ClickEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ClickEventServiceImpl implements ClickEventService {
    private final ClickEventRepository clickEventRepository;

    @Override
    public EventClickStatisticsDto getClickStatistics(String shortCode, LocalDateTime from, LocalDateTime to) {
        if(from != null && to != null && from.isAfter(to)){
            throw new AppException("from позже to", HttpStatus.BAD_REQUEST);
        }

        var stat = clickEventRepository.getStats(shortCode, from, to);
        if(stat == null){
            throw new AppException("Статистика по таким параметрам не найдена", HttpStatus.NOT_FOUND);
        }

        return stat;
    }
}
