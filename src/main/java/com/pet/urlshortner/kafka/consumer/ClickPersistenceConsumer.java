package com.pet.urlshortner.kafka.consumer;

import tools.jackson.databind.ObjectMapper;
import com.pet.urlshortner.dto.ClickEvent;
import com.pet.urlshortner.entity.ClickEventEntity;
import com.pet.urlshortner.repository.ClickEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClickPersistenceConsumer {

    private final ClickEventRepository clickEventRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "test-topic",
            groupId = "click-persistence",
            containerFactory = "batchListenerFactory"
    )
    @Transactional
    public void handle(List<ConsumerRecord<String, String>> records, Acknowledgment ack) {
        List<ClickEventEntity> entities = new ArrayList<>();

        for (ConsumerRecord<String, String> record : records) {
            try {
                ClickEvent event = objectMapper.readValue(record.value(), ClickEvent.class);

                if (clickEventRepository.existsByEventId(UUID.fromString(event.eventId()))) {
                    log.debug("Дубликат события {}, пропускаем", event.eventId());
                    continue;
                }

                entities.add(toEntity(event));
            } catch (Exception e) {
                log.error("Не удалось распарсить click event: {}", record.value(), e);
            }
        }

        if (!entities.isEmpty()) {
            clickEventRepository.saveAll(entities);
        }

        ack.acknowledge();
    }

    private ClickEventEntity toEntity(ClickEvent event) {
        ClickEventEntity entity = new ClickEventEntity();
        entity.setEventId(UUID.fromString(event.eventId()));
        entity.setShortCode(event.shortCode());
        entity.setIp(event.ip());
        entity.setUserAgent(event.userAgent());
        entity.setReferer(event.referer());
        entity.setClickedAt(LocalDateTime.ofInstant(event.timestamp(), ZoneOffset.UTC));
        return entity;
    }
}
