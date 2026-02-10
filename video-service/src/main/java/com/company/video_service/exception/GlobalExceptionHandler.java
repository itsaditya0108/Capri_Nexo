package com.company.video_service.exception;

import com.company.video_service.dto.ApiErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> handleRuntimeException(RuntimeException ex) {
        String message = ex.getMessage();
        int status = 500;
        String error = "INTERNAL_SERVER_ERROR";

        if ("VIDEO_NOT_FOUND".equals(message) || "THUMBNAIL_FILE_NOT_FOUND".equals(message)) {
            status = 404;
            error = "NOT_FOUND";
        } else if ("THUMBNAIL_NOT_READY".equals(message)) {
            status = 404; // Or 202 Accepted if you want client to retry
            error = "NOT_READY";
        } else if (message.startsWith("Chunk upload failed")) {
            status = 400;
            error = "UPLOAD_FAILED";
        }

        return ResponseEntity.status(status).body(new ApiErrorResponse(status, error, message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception ex) {
        return ResponseEntity.status(500).body(new ApiErrorResponse(500, "INTERNAL_SERVER_ERROR", ex.getMessage()));
    }
}
