package com.pet.urlshortner.service.impl;

import com.pet.urlshortner.config.shortenerConfig.ShortenerConfigProperties;
import com.pet.urlshortner.dto.ClickEvent;
import com.pet.urlshortner.dto.CreateLinkRequestDto;
import com.pet.urlshortner.dto.CreateLinkResponseDto;
import com.pet.urlshortner.dto.RedirectResult;
import com.pet.urlshortner.entity.Link;
import com.pet.urlshortner.exception.AppException;
import com.pet.urlshortner.kafka.producer.ClickEventProducer;
import com.pet.urlshortner.repository.LinkRepository;
import com.pet.urlshortner.service.LinkService;
import jakarta.servlet.http.HttpServletRequest;
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
    private final ClickEventProducer eventProducer;

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

    @Override
    public RedirectResult redirect(String shortCode, HttpServletRequest request) {
        var link = linkRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new AppException("Ссылка не найдена", HttpStatus.NOT_FOUND));

        if(!link.isActive()){
            throw new AppException("Ссылка не активна", HttpStatus.GONE);
        }

        if(link.getExpiredAt() != null && link.getExpiredAt().isBefore(LocalDateTime.now())){
            throw new AppException("Срок действия ссылки уже завершился", HttpStatus.GONE);
        }

        eventProducer.send(ClickEvent.of(link.getShortCode(), request));

        return new RedirectResult(link.getOriginalUrl(), link.getRedirectType());
    }

    private String generateShortCode(int length) {
        StringBuilder code = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            code.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }

        return code.toString();
    }
}
