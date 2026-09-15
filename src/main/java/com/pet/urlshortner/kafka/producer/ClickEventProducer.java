package com.pet.urlshortner.kafka.producer;

import com.pet.urlshortner.dto.ClickEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClickEventProducer {

    private static final String TOPIC = "test-topic";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void send(ClickEvent event) {
        String payload = objectMapper.writeValueAsString(event);

        kafkaTemplate.send(TOPIC, event.shortCode(), payload)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error(
                                "Не удалось отправить click event для {}",
                                event.shortCode(),
                                ex
                        );
                    }
                });
    }
}