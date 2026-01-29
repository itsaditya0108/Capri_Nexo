package com.company.chat.repository;

import com.company.chat.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Load latest 50 messages for chat screen.
     */
    List<Message> findTop50ByConversation_ConversationIdAndDeletedFalseOrderByMessageIdDesc(
            Long conversationId
    );


    /**
     * Fetch latest message (used by inbox).
     */
    @Query("""
    SELECT m
    FROM Message m
    WHERE m.conversation.conversationId = :conversationId
    ORDER BY m.messageId DESC
""")
    List<Message> findLatestMessage(
            Long conversationId,
            Pageable pageable
    );


    /**
     * Count unread messages in a conversation for a user.
     */
    @Query("""
    SELECT COUNT(m.messageId)
    FROM Message m
    LEFT JOIN MessageRead mr
        ON mr.message.messageId = m.messageId
       AND mr.userId = :userId
    WHERE m.conversation.conversationId = :conversationId
      AND m.senderId <> :userId
      AND mr.id IS NULL
      AND m.deleted = false
""")
    long countUnread(Long conversationId, Long userId);

    @Query("""
    SELECT m
    FROM Message m
    WHERE m.conversation.conversationId = :conversationId
      AND (:beforeId IS NULL OR m.messageId < :beforeId)
      AND m.deleted = false
    ORDER BY m.messageId DESC
""")
    List<Message> findMessagesPage(
            Long conversationId,
            Long beforeId,
            Pageable pageable
    );

    @Query("""
SELECT m FROM Message m
LEFT JOIN MessageRead mr ON mr.message = m AND mr.userId = :userId
WHERE m.conversation.conversationId = :conversationId
AND mr.id IS NULL
AND m.senderId <> :userId
AND m.deleted = false
""")
    List<Message> findUnreadMessages(
            @Param("conversationId") Long conversationId,
            @Param("userId") Long userId
    );


}


