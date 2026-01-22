package com.company.chat.controller;

import com.company.chat.dto.GroupDashboardResponse;
import com.company.chat.service.GroupDashboardService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupDashboardController {

    private final GroupDashboardService groupDashboardService;

    public GroupDashboardController(
            GroupDashboardService groupDashboardService) {
        this.groupDashboardService = groupDashboardService;
    }

    @GetMapping
    public List<GroupDashboardResponse> myGroups() {

        Long userId = (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return groupDashboardService.getMyGroups(userId);
    }
}
