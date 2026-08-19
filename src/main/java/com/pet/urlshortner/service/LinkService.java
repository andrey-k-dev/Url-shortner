package com.pet.urlshortner.service;

import com.pet.urlshortner.dto.CreateLinkRequestDto;
import com.pet.urlshortner.dto.CreateLinkResponseDto;

public interface LinkService {
    CreateLinkResponseDto createLink(CreateLinkRequestDto request);
}
