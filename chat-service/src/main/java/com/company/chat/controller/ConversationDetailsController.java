package com.company.chat.controller;

import com.company.chat.dto.GroupDetailsResponse;
import com.company.chat.service.GroupConversationService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/conversations")
public class ConversationDetailsController {

    private final GroupConversationService service;

    public ConversationDetailsController(GroupConversationService service) {
        this.service = service;
    }

    @GetMapping("/{id}/details")
    public GroupDetailsResponse details(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return service.getDetails(id, userId, authHeader);
    }

    
}
