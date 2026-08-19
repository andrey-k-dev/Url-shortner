package com.pet.urlshortner.dto;

import com.pet.urlshortner.enums.RedirectType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

public record CreateLinkRequestDto(
        @NotBlank
        @URL
        String originalUrl,

        String customAlias,

        @NotNull
        RedirectType redirectType,

        LocalDateTime expiresAt
) {
}
