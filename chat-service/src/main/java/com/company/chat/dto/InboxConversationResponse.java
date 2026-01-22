package com.company.chat.dto;

import com.company.chat.entity.ConversationType;

import java.time.Instant;
import com.company.chat.entity.ConversationType;
import java.time.Instant;

public class InboxConversationResponse {

    private Long conversationId;
    private ConversationType type;
    private String lastMessage;
    private Long lastMessageSenderId;
    private Instant lastMessageTimestamp;
    private long unreadCount;
    private Long otherUserId;
    private String otherUserName;
    private String groupName;

    public InboxConversationResponse(
            Long conversationId,
            ConversationType type,
            String lastMessage,
            Long lastMessageSenderId,
            Instant lastMessageTimestamp,
            long unreadCount,
            Long otherUserId,
            String otherUserName,
            String groupName) {
        this.conversationId = conversationId;
        this.type = type;
        this.lastMessage = lastMessage;
        this.lastMessageSenderId = lastMessageSenderId;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.unreadCount = unreadCount;
        this.otherUserId = otherUserId;
        this.otherUserName = otherUserName;
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    // getters

    public Long getConversationId() {
        return conversationId;
    }

    public ConversationType getType() {
        return type;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public Long getLastMessageSenderId() {
        return lastMessageSenderId;
    }

    public Instant getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public long getUnreadCount() {
        return unreadCount;
    }

    public Long getOtherUserId() {
        return otherUserId;
    }

    public String getOtherUserName() {
        return otherUserName;
    }
}
