package com.pet.urlshortner.controller;

import com.pet.urlshortner.dto.CreateLinkRequestDto;
import com.pet.urlshortner.dto.CreateLinkResponseDto;
import com.pet.urlshortner.service.LinkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/link")
@RequiredArgsConstructor
@Validated
public class LinkController {
    private final LinkService linkService;

    @PostMapping
    public CreateLinkResponseDto createLink(@RequestBody @Valid CreateLinkRequestDto request) {
        return linkService.createLink(request);
    }
}
