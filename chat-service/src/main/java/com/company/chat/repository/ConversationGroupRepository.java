package com.company.chat.repository;

import com.company.chat.entity.ConversationGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationGroupRepository
        extends JpaRepository<ConversationGroup, Long> {
}
