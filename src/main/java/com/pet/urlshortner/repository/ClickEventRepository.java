package com.pet.urlshortner.repository;

import com.pet.urlshortner.entity.ClickEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClickEventRepository extends JpaRepository<ClickEventEntity, Long> {

    boolean existsByEventId(UUID eventId);
}