package com.company.chat.dto;

import com.company.chat.entity.ConversationType;

import java.time.Instant;

public class CreateConversationResponse {

    private Long conversationId;
    private ConversationType type;
    private Instant createdTimestamp;

    public CreateConversationResponse(
            Long conversationId,
            ConversationType type,
            Instant createdTimestamp
    ) {
        this.conversationId = conversationId;
        this.type = type;
        this.createdTimestamp = createdTimestamp;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public ConversationType getType() {
        return type;
    }

    public Instant getCreatedTimestamp() {
        return createdTimestamp;
    }
}
