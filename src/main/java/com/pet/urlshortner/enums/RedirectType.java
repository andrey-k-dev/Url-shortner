package com.pet.urlshortner.enums;

import org.springframework.http.HttpStatus;

public enum RedirectType {
    PERMANENT(HttpStatus.MOVED_PERMANENTLY),
    TEMPORARY(HttpStatus.FOUND);

    private final HttpStatus status;


    RedirectType(HttpStatus status) {
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
