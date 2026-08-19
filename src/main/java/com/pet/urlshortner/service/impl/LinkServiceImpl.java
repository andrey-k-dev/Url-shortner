package com.pet.urlshortner.service.impl;

import com.pet.urlshortner.config.shortenerConfig.ShortenerConfigProperties;
import com.pet.urlshortner.dto.CreateLinkRequestDto;
import com.pet.urlshortner.dto.CreateLinkResponseDto;
import com.pet.urlshortner.entity.Link;
import com.pet.urlshortner.exception.AppException;
import com.pet.urlshortner.repository.LinkRepository;
import com.pet.urlshortner.service.LinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LinkServiceImpl implements LinkService {
    private final LinkRepository linkRepository;
    private final ShortenerConfigProperties shortenerConfig;

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String ALPHABET =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    @Override
    public CreateLinkResponseDto createLink(CreateLinkRequestDto request) {
        var alias = request.customAlias();

        if (alias != null && linkRepository.existsByShortCode(alias)){
            throw new AppException("URL с таким alias уже существует", HttpStatus.CONFLICT);
        }

        if (alias == null || alias.isBlank()){
            do {
                alias = generateShortCode(shortenerConfig.shortCodeLength());
            } while (linkRepository.existsByShortCode(alias));
        }

        var expiresAt = request.expiresAt() == null ? LocalDateTime.now().plusDays(shortenerConfig.shortLinkLifeTimeDays()) :
                request.expiresAt();

                var link = linkRepository.save(Link.builder()
                        .shortCode(alias)
                        .originalUrl(request.originalUrl())
                        .active(true)
                        .redirectType(request.redirectType())
                        .expiredAt(expiresAt)
                .build());

        return CreateLinkResponseDto.builder()
                .id(link.getId())
                .shortUrl(shortenerConfig.baseUrl() + "/" + link.getShortCode())
                .originalUrl(request.originalUrl())
                .redirectType(request.redirectType().name())
                .createdAt(link.getCreatedAt())
                .expiresAt(link.getExpiredAt())
                .isActive(link.isActive())
                .build();
    }

    private String generateShortCode(int length) {
        StringBuilder code = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            code.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }

        return code.toString();
    }
}
