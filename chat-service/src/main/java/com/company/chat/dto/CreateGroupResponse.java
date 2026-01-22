package com.company.chat.dto;

import com.company.chat.entity.ConversationType;

import java.time.Instant;

public class CreateGroupResponse {

    private Long conversationId;
    private ConversationType type;
    private String name;
    private Instant createdAt;

    public CreateGroupResponse(
            Long conversationId,
            ConversationType type,
            String name,
            Instant createdAt) {
        this.conversationId = conversationId;
        this.type = type;
        this.name = name;
        this.createdAt = createdAt;
    }

    // getters


    public Long getConversationId() {
        return conversationId;
    }

    public ConversationType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
