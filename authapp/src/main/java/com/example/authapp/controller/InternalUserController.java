package com.example.authapp.controller;

import com.example.authapp.dto.UserSearchRequest;
import com.example.authapp.dto.UserSearchResponse;
import com.example.authapp.entity.User;
import com.example.authapp.services.UserSearchService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal/users")
public class InternalUserController {

    private final UserSearchService userSearchService;

    public InternalUserController(UserSearchService userSearchService) {
        this.userSearchService = userSearchService;
    }

    @PostMapping("/search")
    public List<UserSearchResponse> searchUsers(
            @RequestBody UserSearchRequest request,
            @AuthenticationPrincipal User user) {
        return userSearchService.search(
                request.getQuery(),
                request.getLimit(),
                user.getId());
    }

    @PostMapping("/by-ids")
    public List<UserSearchResponse> getUserNamesByIds(
            @RequestBody java.util.Set<Long> userIds) {
        return userSearchService.getUsersByIds(userIds);
    }

}
