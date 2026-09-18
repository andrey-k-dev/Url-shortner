package com.pet.urlshortner.cashe;

import com.pet.urlshortner.dto.LinkCache;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LinkCacheService {

    private static final String KEY_PREFIX = "link:";

    private final RedisTemplate<String, LinkCache> redisTemplate;

    public Optional<LinkCache> get(String shortCode) {
        var key = KEY_PREFIX + shortCode;

        return Optional.ofNullable(
                redisTemplate.opsForValue().get(key)
        );
    }

    public void put(LinkCache link) {
        var key = KEY_PREFIX + link.shortCode();

        redisTemplate.opsForValue().set(
                key,
                link,
                Duration.ofHours(1)
        );
    }

    public void evict(String shortCode) {
        var key = KEY_PREFIX + shortCode;

        redisTemplate.delete(key);
    }
}
