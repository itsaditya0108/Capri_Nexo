package com.company.chat.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class ImageValidationClient {

    private final RestTemplate restTemplate;

    @Value("${image.service.url}")
    private String imageServiceUrl;

    public ImageValidationClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean validateImage(Long imageId, Long userId, String authHeader) {
        if (imageId == null || userId == null)
            return false;

        try {
            HttpHeaders headers = new HttpHeaders();
            // Pass the auth token if we have it, or at least some form of auth
            // Ideally we need to forward the Bearer token
            if (authHeader != null && !authHeader.isEmpty()) {
                headers.set("Authorization", authHeader);
            }

            // We can also pass userId in header/param if needed, but the endpoint expects
            // authenticated user from token
            // so we rely on the forwarded token.

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            String url = imageServiceUrl + "/api/images/" + imageId + "/validate";

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {
                    });

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Boolean isValid = (Boolean) response.getBody().get("valid");
                return isValid != null && isValid;
            }

        } catch (Exception e) {
            // Log error?
            // If service is down or 404/403, we assume invalid
        }

        return false;
    }
}
