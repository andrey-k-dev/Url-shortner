package com.pet.urlshortner.repository;

import com.pet.urlshortner.dto.EventClickStatisticsDto;
import com.pet.urlshortner.entity.ClickEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ClickEventRepository extends JpaRepository<ClickEventEntity, Long> {

    boolean existsByEventId(UUID eventId);
    @Query("""
        SELECT
            c.shortCode,
            COUNT(c.id),
            COUNT(DISTINCT c.ip),
            MAX(c.createdAt),
            MIN(c.createdAt)
        FROM ClickEventEntity c     
        WHERE c.shortCode = :shortCode
            AND (CAST(:from AS timestamp) IS NULL OR c.createdAt >= :from)
            AND (CAST(:to AS timestamp) IS NULL OR c.createdAt <= :to)   
        GROUP BY c.shortCode     
       """)
    EventClickStatisticsDto getStats(
            @Param("shortCode") String shortCode,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

}