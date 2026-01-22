package com.company.chat.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
        name = "message_reads",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"message_id", "user_id"})
        },
        indexes = {
                @Index(name = "idx_user_message", columnList = "user_id, message_id")
        }
)
public class MessageRead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(
            name = "read_timestamp",
            nullable = false,
            updatable = false
    )
    private Instant readTimestamp;

    @PrePersist
    protected void onRead() {
        this.readTimestamp = Instant.now();
    }

//    getter / setter


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Instant getReadTimestamp() {
        return readTimestamp;
    }

    public void setReadTimestamp(Instant readTimestamp) {
        this.readTimestamp = readTimestamp;
    }
}
