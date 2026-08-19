package com.pet.urlshortner.config.shortenerConfig;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ShortenerConfigProperties.class)
public class ShortenerConfig {
}
