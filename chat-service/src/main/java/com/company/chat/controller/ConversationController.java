package com.company.chat.controller;

import com.company.chat.dto.CreateConversationRequest;
import com.company.chat.dto.CreateConversationResponse;
import com.company.chat.dto.UserSearchResponse;
import com.company.chat.entity.Conversation;
import com.company.chat.service.ConversationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @GetMapping("/users/search")
    public List<UserSearchResponse> searchUsers(
            @RequestParam String q,
            HttpServletRequest request
    ) {
        String authHeader = request.getHeader("Authorization");
        Long currentUserId = currentUserId();

        return conversationService.searchUsers(q, currentUserId, authHeader);
    }

    private Long currentUserId() {
        return (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }


}


