package com.company.chat.controller;

import com.company.chat.dto.CreateGroupRequest;
import com.company.chat.dto.CreateGroupResponse;
import com.company.chat.dto.GroupDetailsResponse;
import com.company.chat.service.GroupConversationService;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/conversations")
public class GroupConversationController {

    private final GroupConversationService groupService;

    public GroupConversationController(
            GroupConversationService groupService) {
        this.groupService = groupService;
    }


    @PostMapping("/groups")
    public CreateGroupResponse createGroup(
            @Valid @RequestBody CreateGroupRequest request) {

        Long userId = (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return groupService.createGroup(userId, request);
    }

}
