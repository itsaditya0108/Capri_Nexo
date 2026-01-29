package com.company.image_service.dto;

import java.time.LocalDateTime;

public class ApiErrorResponse {

    private String error;
    private String message;
    private LocalDateTime timestamp;

    public ApiErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
