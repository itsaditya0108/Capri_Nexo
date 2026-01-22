package com.company.chat.dto;

import java.time.Instant;

public class GroupDashboardResponse {

    private Long conversationId;
    private String groupName;
    private int memberCount;
    private String lastMessage;
    private Instant lastMessageAt;

    public GroupDashboardResponse(
            Long conversationId,
            String groupName,
            int memberCount,
            String lastMessage,
            Instant lastMessageAt) {
        this.conversationId = conversationId;
        this.groupName = groupName;
        this.memberCount = memberCount;
        this.lastMessage = lastMessage;
        this.lastMessageAt = lastMessageAt;
    }

    // getters

    public Long getConversationId() {
        return conversationId;
    }

    public String getGroupName() {
        return groupName;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public Instant getLastMessageAt() {
        return lastMessageAt;
    }
}
