package com.company.image_service.exception;

import com.company.image_service.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class GlobalExceptionHandler {

        // -------------------------
        // Image not found / access denied
        // -------------------------
        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex) {

                ApiErrorResponse error = new ApiErrorResponse(
                                404,
                                "NOT_FOUND",
                                ex.getMessage());

                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(error);
        }

        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<ApiErrorResponse> handleRuntime(RuntimeException ex) {

                if (ex.getMessage().contains("not found")) {
                        ApiErrorResponse error = new ApiErrorResponse(
                                        404,
                                        "NOT_FOUND",
                                        ex.getMessage());
                        return ResponseEntity
                                        .status(HttpStatus.NOT_FOUND)
                                        .body(error);
                }

                ApiErrorResponse error = new ApiErrorResponse(
                                400,
                                "REQUEST_FAILED",
                                ex.getMessage());

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(error);
        }

        // -------------------------
        // File too large (Spring multipart)
        // -------------------------
        @ExceptionHandler(MaxUploadSizeExceededException.class)
        public ResponseEntity<ApiErrorResponse> handleMaxSize(MaxUploadSizeExceededException ex) {

                ApiErrorResponse error = new ApiErrorResponse(
                                413,
                                "PAYLOAD_TOO_LARGE",
                                "Uploaded file exceeds allowed size");

                return ResponseEntity
                                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                                .body(error);
        }

        // -------------------------
        // Validation errors
        // -------------------------
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex) {

                String details = ex.getBindingResult().getFieldErrors().stream()
                                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                                .collect(java.util.stream.Collectors.joining(", "));

                ApiErrorResponse error = new ApiErrorResponse(
                                400,
                                "VALIDATION_FAILED",
                                details);

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(error);
        }

        // -------------------------
        // Fallback (safety net)
        // -------------------------
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex) {

                ApiErrorResponse error = new ApiErrorResponse(
                                500,
                                "INTERNAL_ERROR",
                                "Something went wrong");

                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(error);
        }
}
