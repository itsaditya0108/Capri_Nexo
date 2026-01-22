package com.company.chat.repository;

import com.company.chat.entity.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("""
        SELECT c
        FROM Conversation c
        JOIN ConversationMember cm
            ON cm.conversation.conversationId = c.conversationId
        WHERE cm.userId = :userId
        ORDER BY c.lastMessageTimestamp DESC NULLS LAST
    """)
    Page<Conversation> findUserConversations(
            Long userId,
            Pageable pageable
    );

    @Query("""
SELECT c FROM Conversation c
JOIN ConversationMember cm1 ON cm1.conversation = c
JOIN ConversationMember cm2 ON cm2.conversation = c
WHERE c.type = 'PRIVATE'
AND cm1.userId = :user1
AND cm2.userId = :user2
""")
    Optional<Conversation> findPrivateConversation(Long user1, Long user2);


    @Query("""
SELECT c
FROM Conversation c
JOIN ConversationMember cm
  ON cm.conversation.conversationId = c.conversationId
WHERE cm.userId = :userId
  AND c.type = 'GROUP'
  AND cm.leftTimestamp IS NULL
""")
    List<Conversation> findUserGroupConversations(@Param("userId") Long userId);

}



