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
    private final ConversationRepository conversationRepository;
    private final ConversationMemberRepository conversationMemberRepository;

    public ConversationService(
            AuthUserClient authUserClient,
            ConversationRepository conversationRepository,
            ConversationMemberRepository conversationMemberRepository) {
        this.authUserClient = authUserClient;
        this.conversationRepository = conversationRepository;
        this.conversationMemberRepository = conversationMemberRepository;
    }

    public List<UserSearchResponse> searchUsers(
            String query,
            Long selfUserId,
            String authHeader) {
        if (query == null || query.trim().length() < 2) {
            return List.of();
        }

        return authUserClient.searchUsers(
                query.trim(),
                authHeader);
    }

    @Transactional
    public com.company.chat.dto.CreateConversationResponse createConversation(
            com.company.chat.dto.CreateConversationRequest request, Long initiatorUserId) {
        // 1. If PRIVATE, check if already exists
        if (request.getType() == ConversationType.PRIVATE) {
            Optional<Conversation> existing = conversationRepository.findPrivateConversation(
                    initiatorUserId,
                    request.getParticipantUserId());

            if (existing.isPresent()) {
                Conversation c = existing.get();
                return new com.company.chat.dto.CreateConversationResponse(
                        c.getConversationId(),
                        c.getType(),
                        c.getCreatedTimestamp());
            }
        }

        // 2. Create new Conversation
        Conversation conv = new Conversation();
        conv.setType(request.getType());
        conv.setCreatedBy(initiatorUserId);
        // lastMessageId null initially

        conv = conversationRepository.save(conv);

        // 3. Add members
        // Initiator
        ConversationMember m1 = new ConversationMember(conv, initiatorUserId);
        conversationMemberRepository.save(m1);

        // Process based on type
        if (request.getType() == ConversationType.PRIVATE) {
            ConversationMember m2 = new ConversationMember(conv, request.getParticipantUserId());
            conversationMemberRepository.save(m2);
        }

        return new com.company.chat.dto.CreateConversationResponse(
                conv.getConversationId(),
                conv.getType(),
                conv.getCreatedTimestamp());
    }
}
