package com.pet.urlshortner.config.shortenerConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pet.urlshortner.dto.LinkCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, LinkCache> redisTemplate(
            RedisConnectionFactory connectionFactory
    ) {
        var template = new RedisTemplate<String, LinkCache>();

        template.setConnectionFactory(connectionFactory);

        var keySerializer = new StringRedisSerializer();
        var valueSerializer = new JacksonJsonRedisSerializer<>(LinkCache.class);

        template.setKeySerializer(keySerializer);
        template.setValueSerializer(valueSerializer);

        template.setHashKeySerializer(keySerializer);
        template.setHashValueSerializer(valueSerializer);

        template.afterPropertiesSet();

        return template;
    }
}
