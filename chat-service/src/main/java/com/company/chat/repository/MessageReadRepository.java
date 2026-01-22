package com.company.chat.repository;


import com.company.chat.entity.Message;
import com.company.chat.entity.MessageRead;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageReadRepository extends JpaRepository<MessageRead, Long> {

    boolean existsByMessage_MessageIdAndUserId(Long messageId, Long userId);

    List<MessageRead> findByUserId(Long userId);
}
