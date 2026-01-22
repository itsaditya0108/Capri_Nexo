package com.company.chat.service;

import com.company.chat.dto.GroupDashboardResponse;
import com.company.chat.entity.Conversation;
import com.company.chat.entity.ConversationGroup;
import com.company.chat.entity.Message;
import com.company.chat.repository.ConversationGroupRepository;
import com.company.chat.repository.ConversationMemberRepository;
import com.company.chat.repository.ConversationRepository;
import com.company.chat.repository.MessageRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GroupDashboardService {

    private final ConversationRepository conversationRepository;
    private final ConversationGroupRepository groupRepository;
    private final ConversationMemberRepository memberRepository;
    private final MessageRepository messageRepository;

    public GroupDashboardService(
            ConversationRepository conversationRepository,
            ConversationGroupRepository groupRepository,
            ConversationMemberRepository memberRepository,
            MessageRepository messageRepository) {
        this.conversationRepository = conversationRepository;
        this.groupRepository = groupRepository;
        this.memberRepository = memberRepository;
        this.messageRepository = messageRepository;
    }

    public List<GroupDashboardResponse> getMyGroups(Long userId) {

        List<Conversation> groups =
                conversationRepository.findUserGroupConversations(userId);

        return groups.stream().map(conversation -> {

            ConversationGroup group =
                    groupRepository.findById(conversation.getConversationId())
                            .orElseThrow();

            int memberCount =
                    memberRepository.countActiveMembers(
                            conversation.getConversationId());

            Message lastMessage =
                    messageRepository.findLatestMessage(
                                    conversation.getConversationId(),
                                    PageRequest.of(0, 1))
                            .stream()
                            .findFirst()
                            .orElse(null);

            return new GroupDashboardResponse(
                    conversation.getConversationId(),
                    group.getName(),
                    memberCount,
                    lastMessage != null ? lastMessage.getContent() : null,
                    lastMessage != null ? lastMessage.getCreatedTimestamp() : null
            );
        }).toList();
    }
}
