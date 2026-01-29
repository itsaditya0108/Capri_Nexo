package com.company.chat.service;

import com.company.chat.client.AuthUserClient;
import com.company.chat.dto.InboxConversationResponse;
import com.company.chat.entity.Conversation;
import com.company.chat.entity.ConversationMember;
import com.company.chat.entity.ConversationType;
import com.company.chat.entity.Message;
import com.company.chat.repository.ConversationMemberRepository;
import com.company.chat.repository.ConversationRepository;
import com.company.chat.repository.MessageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class InboxService {

        private final ConversationRepository conversationRepository;
        private final MessageRepository messageRepository;
        private final ConversationMemberRepository memberRepository;
        private final AuthUserClient authUserClient;
        private final com.company.chat.repository.ConversationGroupRepository groupRepository;

        public InboxService(
                        ConversationRepository conversationRepository,
                        MessageRepository messageRepository,
                        ConversationMemberRepository memberRepository,
                        AuthUserClient authUserClient,
                        com.company.chat.repository.ConversationGroupRepository groupRepository) {
                this.conversationRepository = conversationRepository;
                this.messageRepository = messageRepository;
                this.memberRepository = memberRepository;
                this.authUserClient = authUserClient;
                this.groupRepository = groupRepository;
        }

        public List<InboxConversationResponse> getInbox(
                        Long userId,
                        int page,
                        int size,
                        String authHeader) {

                /* ================= 1️⃣ LOAD CONVERSATIONS ================= */

                Page<Conversation> conversations = conversationRepository.findUserConversations(
                                userId,
                                PageRequest.of(page, size));

                if (conversations.isEmpty()) {
                        return List.of();
                }

                /* ================= 2️⃣ FIND OTHER USER IDS & GROUP NAMES ================= */

                Map<Long, Long> conversationToOtherUser = new HashMap<>();
                List<Long> groupIds = new ArrayList<>();

                for (Conversation conversation : conversations) {
                        if (conversation.getType() == ConversationType.PRIVATE) {
                                memberRepository
                                                .findFirstByConversation_ConversationIdAndUserIdNot(
                                                                conversation.getConversationId(),
                                                                userId)
                                                .map(ConversationMember::getUserId)
                                                .ifPresent(otherUserId -> conversationToOtherUser.put(
                                                                conversation.getConversationId(),
                                                                otherUserId));
                        } else if (conversation.getType() == ConversationType.GROUP) {
                                groupIds.add(conversation.getConversationId());
                        }
                }

                Set<Long> otherUserIds = new HashSet<>(conversationToOtherUser.values());

                /* ================= 3️⃣ RESOLVE NAMES (USERS & GROUPS) ================= */

                Map<Long, String> userNamesById = authUserClient.getUserNamesByIds(otherUserIds, authHeader);

                // Fetch group names
                Map<Long, String> groupNamesById = new HashMap<>();
            if (!groupIds.isEmpty()) {
                groupRepository.findByConversationIdIn(groupIds)
                        .forEach(g ->
                                groupNamesById.put(g.getConversationId(), g.getName())
                        );
            }

            /* ================= 4️⃣ BUILD INBOX RESPONSE ================= */

                return conversations.stream()
                                .map(conversation -> {

                                        // Last message
                                        List<Message> latest = messageRepository.findLatestMessage(
                                                        conversation.getConversationId(),
                                                        PageRequest.of(0, 1));

                                        Message lastMessage = latest.isEmpty() ? null : latest.get(0);

                                        // Unread count
                                        long unread = messageRepository.countUnread(
                                                        conversation.getConversationId(),
                                                        userId);

                                        // Other user
                                        Long otherUserId = null;
                                        String otherUserName = null;
                                        String groupName = null;

                                        if (conversation.getType() == ConversationType.PRIVATE) {
                                                otherUserId = conversationToOtherUser
                                                                .get(conversation.getConversationId());
                                            otherUserName = userNamesById.getOrDefault(otherUserId, "Unknown");
                                        } else if (conversation.getType() == ConversationType.GROUP) {
                                            groupName = groupNamesById.getOrDefault(
                                                    conversation.getConversationId(),
                                                    "Group"
                                            );
                                        }

                                        return new InboxConversationResponse(
                                                        conversation.getConversationId(),
                                                        conversation.getType(),
                                                        lastMessage != null ? lastMessage.getContent() : null,
                                                        lastMessage != null ? lastMessage.getSenderId() : null,
                                                        lastMessage != null ? lastMessage.getCreatedTimestamp() : null,
                                                        unread,
                                                        otherUserId,
                                                        otherUserName,
                                                        groupName);
                                })
                                .toList();
        }
}
