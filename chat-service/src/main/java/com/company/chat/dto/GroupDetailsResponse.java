package com.company.chat.dto;

import java.util.List;

public class GroupDetailsResponse {

    private Long conversationId;
    private String groupName;
    private List<UserSummary> members;
    private boolean admin;

    public GroupDetailsResponse(
            Long conversationId,
            String groupName,
            List<UserSummary> members, boolean isAdmin) {
        this.conversationId = conversationId;
        this.groupName = groupName;
        this.members = members;
        this.admin = isAdmin;
    }

    public boolean isAdmin() {
        return admin;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public String getGroupName() {
        return groupName;
    }

    public List<UserSummary> getMembers() {
        return members;
    }
}
