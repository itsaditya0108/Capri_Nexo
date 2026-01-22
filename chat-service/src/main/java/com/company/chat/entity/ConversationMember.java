package com.company.chat.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
        name = "conversation_members",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"conversation_id", "user_id"})
        }
)
public class ConversationMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "joined_timestamp", nullable = false, updatable = false)
    private Instant joinedTimestamp = Instant.now();

    @Column(name = "left_timestamp")
    private Instant leftTimestamp;

    // ✅ REQUIRED BY JPA
    protected ConversationMember() {
    }

    // ✅ CONVENIENCE CONSTRUCTOR (for your service code)
    public ConversationMember(Conversation conversation, Long userId) {
        this.conversation = conversation;
        this.userId = userId;
    }

    // ----- getters & setters -----


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Instant getJoinedTimestamp() {
        return joinedTimestamp;
    }

    public void setJoinedTimestamp(Instant joinedTimestamp) {
        this.joinedTimestamp = joinedTimestamp;
    }

    public Instant getLeftTimestamp() {
        return leftTimestamp;
    }

    public void setLeftTimestamp(Instant leftTimestamp) {
        this.leftTimestamp = leftTimestamp;
    }
}
