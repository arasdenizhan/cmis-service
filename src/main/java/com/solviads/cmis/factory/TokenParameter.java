package com.solviads.cmis.factory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.logging.Logger;


public class TokenParameter {

    static Logger logger = Logger.getLogger(TokenParameter.class.getName());

    private String token;
    private String refreshToken;
    private Long expiresIn;
    private final LocalDateTime expirePoint;

    public TokenParameter(String token, String refreshToken, Long expiresIn) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.expirePoint = LocalDateTime.ofInstant(Instant.now().plus(expiresIn, ChronoUnit.SECONDS), ZoneId.systemDefault());
    }

    public boolean isTokenExpired() {
        return expirePoint.isBefore(LocalDateTime.now());
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

}
