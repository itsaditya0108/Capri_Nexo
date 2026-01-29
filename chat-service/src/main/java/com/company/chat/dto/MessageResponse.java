package com.company.chat.dto;

import java.time.Instant;

public class MessageResponse {

    private Long messageId;
    private Long conversationId;
    private Long senderId;
    private String content;
    private Instant createdTimestamp;

    private boolean read;
    private String senderName;

    public MessageResponse(
            Long messageId,
            Long conversationId,
            Long senderId,
            String content,
            Instant createdTimestamp) {
        this.messageId = messageId;
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.content = content;
        this.createdTimestamp = createdTimestamp;
        this.read = false;
        this.senderName = "User " + senderId;
    }

    public MessageResponse(
            Long messageId,
            Long conversationId,
            Long senderId,
            String content,
            Instant createdTimestamp,
            boolean read) {
        this(messageId, conversationId, senderId, content, createdTimestamp, read, "User " + senderId);
    }

    public MessageResponse(
            Long messageId,
            Long conversationId,
            Long senderId,
            String content,
            Instant createdTimestamp,
            boolean read,
            String senderName) {
        this.messageId = messageId;
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.content = content;
        this.createdTimestamp = createdTimestamp;
        this.read = read;
        this.senderName = senderName;
    }

    public Long getMessageId() {
        return messageId;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public String getContent() {
        return content;
    }

    public Instant getCreatedTimestamp() {
        return createdTimestamp;
    }

    public boolean isRead() {
        return read;
    }

    public String getSenderName() {
        return senderName;
    }
}
