package com.company.chat.repository;

import com.company.chat.entity.ConversationMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;



public interface ConversationMemberRepository
        extends JpaRepository<ConversationMember, Long> {

    List<ConversationMember> findByUserId(Long userId);

    Optional<ConversationMember> findByConversation_ConversationIdAndUserId(
            Long conversationId, Long userId);

    Optional<ConversationMember> findFirstByConversation_ConversationIdAndUserIdNot(
            Long conversationId, Long userId);


    List<ConversationMember> findByConversation_ConversationId(
            Long conversationId
    );
    @Query("""
SELECT COUNT(cm)
FROM ConversationMember cm
WHERE cm.conversation.conversationId = :conversationId
  AND cm.leftTimestamp IS NULL
""")
    int countActiveMembers(@Param("conversationId") Long conversationId);

}

