package com.company.chat.controller;

import com.company.chat.dto.GroupDetailsResponse;
import com.company.chat.dto.UserSearchResponse;
import com.company.chat.service.ConversationService;
import com.company.chat.service.GroupConversationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService conversationService;
    private final GroupConversationService groupService;

    public ConversationController(
            ConversationService conversationService,
            GroupConversationService groupService) {
        this.conversationService = conversationService;
        this.groupService = groupService;
    }

    @GetMapping("/users/search")
    public List<UserSearchResponse> searchUsers(
            @RequestParam String q,
            HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        Long currentUserId = currentUserId();

        return conversationService.searchUsers(q, currentUserId, authHeader);
    }

    @PostMapping
    public com.company.chat.dto.CreateConversationResponse createConversation(
            @RequestBody com.company.chat.dto.CreateConversationRequest request) {
        return conversationService.createConversation(request, currentUserId());
    }

    private Long currentUserId() {
        return (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}
