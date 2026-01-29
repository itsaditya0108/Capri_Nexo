package com.company.chat.controller;

import com.company.chat.dto.AddGroupMemberRequest;
import com.company.chat.service.GroupConversationService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/conversations")
public class GroupMemberController {

    private final GroupConversationService groupService;

    public GroupMemberController(GroupConversationService groupService) {
        this.groupService = groupService;
    }

    @PostMapping("/{conversationId}/members")
    public void addMember(
            @PathVariable Long conversationId,
            @RequestBody AddGroupMemberRequest request) {

        Long adminId = (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        groupService.addMember(
                conversationId,
                adminId,
                request.getUserId()
        );
    }
}
