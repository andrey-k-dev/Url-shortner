package com.pet.urlshortner.service.impl;

import com.pet.urlshortner.cashe.LinkCacheService;
import com.pet.urlshortner.config.shortenerConfig.ShortenerConfigProperties;
import com.pet.urlshortner.dto.*;
import com.pet.urlshortner.entity.Link;
import com.pet.urlshortner.exception.AppException;
import com.pet.urlshortner.kafka.producer.ClickEventProducer;
import com.pet.urlshortner.repository.LinkRepository;
import com.pet.urlshortner.service.LinkService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LinkServiceImpl implements LinkService {
    private final LinkRepository linkRepository;
    private final ShortenerConfigProperties shortenerConfig;
    private final ClickEventProducer eventProducer;
    private final LinkCacheService linkCacheService;

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String ALPHABET =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    @Override
    public CreateLinkResponseDto createLink(CreateLinkRequestDto request) {
        var isCustomAlias = request.customAlias() != null && !request.customAlias().isBlank();
        var alias = request.customAlias();

        if (isCustomAlias && linkRepository.existsByShortCode(alias)){
            throw new AppException("URL с таким alias уже существует", HttpStatus.CONFLICT);
        }

        if (alias == null || alias.isBlank()){
                alias = generateShortCode(shortenerConfig.shortCodeLength());
        }

        var expiresAt = request.expiresAt() == null ? LocalDateTime.now().plusDays(shortenerConfig.shortLinkLifeTimeDays()) :
                request.expiresAt();

        var link = Link.builder()
                .shortCode(alias)
                .originalUrl(request.originalUrl())
                .active(true)
                .redirectType(request.redirectType())
                .expiredAt(expiresAt)
                .build();

        try{
            linkRepository.save(link);
        }
        catch (DataIntegrityViolationException e){
           if (isCustomAlias){
                throw new AppException("URL с таким alias уже существует", HttpStatus.CONFLICT);
           }

            link.setShortCode(generateShortCode(shortenerConfig.shortCodeLength()));
            linkRepository.save(link);
        }

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
        var link = linkCacheService.get(shortCode)
                .orElseGet(() -> {
                    var linkEntity = linkRepository.findByShortCode(shortCode)
                            .orElseThrow(() -> new AppException("Ссылка не найдена", HttpStatus.NOT_FOUND));

                    var cache = new LinkCache(
                            linkEntity.getShortCode(),
                            linkEntity.getOriginalUrl(),
                            linkEntity.getRedirectType(),
                            linkEntity.isActive(),
                            linkEntity.getExpiredAt()
                    );

                    linkCacheService.put(cache);

                    return cache;
                });

        if(!link.active()){
            throw new AppException("Ссылка не активна", HttpStatus.GONE);
        }

        if(link.expiredAt() != null && link.expiredAt().isBefore(LocalDateTime.now())){
            throw new AppException("Срок действия ссылки уже завершился", HttpStatus.GONE);
        }

        eventProducer.send(ClickEvent.of(link.shortCode(), request));

        return new RedirectResult(link.originalUrl(), link.redirectType());
    }

    @Override
    @Transactional
    public void deactivateLink(String shortCode) {
        var link = linkRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new AppException("Короткая ссылка с таким shortCode не найдена", HttpStatus.NOT_FOUND));

        if(!link.isActive()){
            throw new AppException("Ссылка уже не активна",  HttpStatus.CONFLICT);
        }

        link.setActive(false);
        linkCacheService.evict(shortCode);
    }

    private String generateShortCode(int length) {
        StringBuilder code = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            code.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }

        return code.toString();
    }
}
