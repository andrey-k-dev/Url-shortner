package com.pet.urlshortner.dto;

import com.pet.urlshortner.enums.RedirectType;

public record RedirectResult(
        String originalUrl,
        RedirectType redirectType
) {
}