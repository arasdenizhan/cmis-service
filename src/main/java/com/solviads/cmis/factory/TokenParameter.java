package com.solviads.cmis.factory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;


public record TokenParameter(String token, String refreshToken, Long expiresIn) {

    public boolean isTokenExpired() {
        return LocalDateTime.ofInstant(Instant.now().plus(expiresIn, ChronoUnit.SECONDS), ZoneId.systemDefault()).isBefore(LocalDateTime.now());
    }

}
