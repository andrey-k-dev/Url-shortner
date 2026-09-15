package com.pet.urlshortner.service;

import com.pet.urlshortner.dto.CreateLinkRequestDto;
import com.pet.urlshortner.dto.CreateLinkResponseDto;
import com.pet.urlshortner.dto.RedirectResult;
import jakarta.servlet.http.HttpServletRequest;

public interface LinkService {
    CreateLinkResponseDto createLink(CreateLinkRequestDto request);
    RedirectResult redirect(String shortCode, HttpServletRequest request);
}
