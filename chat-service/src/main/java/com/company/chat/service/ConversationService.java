package com.company.chat.service;

import com.company.chat.client.AuthUserClient;
import com.company.chat.dto.UserSearchResponse;
import com.company.chat.entity.Conversation;
import com.company.chat.entity.ConversationMember;
import com.company.chat.entity.ConversationType;
import com.company.chat.repository.ConversationMemberRepository;
import com.company.chat.repository.ConversationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;



@Service
public class ConversationService {

    private final AuthUserClient authUserClient;

    public ConversationService(AuthUserClient authUserClient) {
        this.authUserClient = authUserClient;
    }

    public List<UserSearchResponse> searchUsers(
            String query,
            Long selfUserId,
            String authHeader
    ) {
        if (query == null || query.trim().length() < 2) {
            return List.of();
        }

        return authUserClient.searchUsers(
                query.trim(),
                authHeader
        );
    }
}
