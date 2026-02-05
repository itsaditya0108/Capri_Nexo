package com.company.chat.dto;

import java.awt.*;
import java.time.LocalDateTime;

public class ChatMessageResponse {

    private Long messageId;
    private Long conversationId;
    private Long senderId;
    private String senderName;

    private TrayIcon.MessageType messageType;   // TEXT / IMAGE
    private String content;            // text or caption
    private Long imageId;

    private String thumbnailUrl;       // only for IMAGE
    private String downloadUrl;        // only for IMAGE

    private LocalDateTime createdTimestamp;
    private boolean read;

    // getters & setters
}
