package com.company.chat.dto;

import com.company.chat.model.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SendMessageRequest {

    // @NotBlank removed to allow IMAGE messages with empty content. Validation
    // logic should enforce content for TEXT type.
    @Size(max = 5000)
    private String content;

    private MessageType type; // TEXT or IMAGE
    private Long imageId;

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
