package com.company.chat.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "conversation_groups")
public class ConversationGroup {

    @Id
    @Column(name = "conversation_id")
    private Long conversationId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(
            name = "created_timestamp",
            insertable = false,
            updatable = false
    )
    private Instant createdTimestamp;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    // getters/setters


    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }
}
