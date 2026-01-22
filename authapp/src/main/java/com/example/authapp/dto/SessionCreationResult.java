package com.example.authapp.dto;

import com.example.authapp.entity.UserSession;

public class SessionCreationResult {
    private final UserSession session;
    private final String rawRefreshToken;

    public SessionCreationResult(UserSession session, String rawRefreshToken) {
        this.session = session;
        this.rawRefreshToken = rawRefreshToken;
    }

    public UserSession getSession() {
        return session;
    }

    public String getRawRefreshToken() {
        return rawRefreshToken;
    }
}
