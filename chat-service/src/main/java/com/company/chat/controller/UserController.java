package com.company.chat.controller;

import com.company.chat.client.AuthUserClient;
import com.company.chat.dto.UserSearchResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final AuthUserClient authUserClient;

    public UserController(AuthUserClient authUserClient) {
        this.authUserClient = authUserClient;
    }

    /**
     * Search users by name or email
     * GET /api/users/search?q=john
     */
    @GetMapping("/search")
    public ResponseEntity<List<UserSearchResponse>> searchUsers(
            @RequestParam("q") String query,
            @RequestHeader("Authorization") String authHeader) {
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<UserSearchResponse> users = authUserClient.searchUsers(
                query.trim(),
                authHeader);

        return ResponseEntity.ok(users);
    }
}
