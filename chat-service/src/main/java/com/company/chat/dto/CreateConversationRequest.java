package com.company.chat.dto;

import com.company.chat.entity.ConversationType;
import jakarta.validation.constraints.NotNull;

public class CreateConversationRequest {

    @NotNull
    private ConversationType type;

    @NotNull
    private Long participantUserId;

    public ConversationType getType() {
        return type;
    }

    public Long getParticipantUserId() {
        return participantUserId;
    }
}
