package com.pet.urlshortner.controller;

import com.pet.urlshortner.dto.CreateLinkRequestDto;
import com.pet.urlshortner.dto.CreateLinkResponseDto;
import com.pet.urlshortner.service.LinkService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@Validated
public class LinkController {
    private final LinkService linkService;

    @PostMapping("/api/v1/link")
    public CreateLinkResponseDto createLink(@RequestBody @Valid CreateLinkRequestDto request) {
        return linkService.createLink(request);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode, HttpServletRequest request) {
        var redirectResult = linkService.redirect(shortCode, request);

        return ResponseEntity
                .status(redirectResult.redirectType().getStatus())
                .location(URI.create(redirectResult.originalUrl()))
                .build();
    }
}
