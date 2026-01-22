package com.company.chat.client;

import com.company.chat.dto.UserSearchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class AuthUserClient {

    private final RestTemplate restTemplate;

    @Value("${auth.service.base-url}")
    private String authBaseUrl;

    public AuthUserClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /* ================= SEARCH USERS ================= */

    public List<UserSearchResponse> searchUsers(
            String query,
            String authHeader
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "query", query,
                "limit", 10
        );

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(body, headers);

        ResponseEntity<UserSearchResponse[]> response =
                restTemplate.postForEntity(
                        authBaseUrl + "/internal/users/search",
                        entity,
                        UserSearchResponse[].class
                );

        return Arrays.asList(
                Objects.requireNonNull(response.getBody())
        );
    }

    /* ================= GET USERS BY IDS (NEW) ================= */

    public Map<Long, String> getUserNamesByIds(
            Set<Long> userIds,
            String authHeader
    ) {
        if (userIds.isEmpty()) return Map.of();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Set<Long>> entity =
                new HttpEntity<>(userIds, headers);

        ResponseEntity<UserSearchResponse[]> response =
                restTemplate.postForEntity(
                        authBaseUrl + "/internal/users/by-ids",
                        entity,
                        UserSearchResponse[].class
                );

        return Arrays.stream(response.getBody())
                .collect(Collectors.toMap(
                        UserSearchResponse::userId,
                        UserSearchResponse::name
                ));
    }

}
