package com.company.chat.service;

import com.company.chat.dto.CreateGroupRequest;
import com.company.chat.dto.CreateGroupResponse;
import com.company.chat.entity.Conversation;
import com.company.chat.entity.ConversationGroup;
import com.company.chat.entity.ConversationMember;
import com.company.chat.entity.ConversationType;
import com.company.chat.repository.ConversationGroupRepository;
import com.company.chat.repository.ConversationMemberRepository;
import com.company.chat.repository.ConversationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class GroupConversationService {

    private final ConversationRepository conversationRepository;
    private final ConversationGroupRepository groupRepository;
    private final ConversationMemberRepository memberRepository;

    public GroupConversationService(
            ConversationRepository conversationRepository,
            ConversationGroupRepository groupRepository,
            ConversationMemberRepository memberRepository) {
        this.conversationRepository = conversationRepository;
        this.groupRepository = groupRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public CreateGroupResponse createGroup(
            Long creatorId,
            CreateGroupRequest request) {

        // 1️⃣ Clean member list
        Set<Long> members = new HashSet<>(request.getMemberIds());
        members.remove(creatorId);

        if (members.size() < 2) {
            throw new IllegalArgumentException(
                    "Group must have at least 2 members");
        }

        // 2️⃣ Create conversation
        Conversation conversation = new Conversation();
        conversation.setType(ConversationType.GROUP);
        conversation.setCreatedBy(creatorId);

        conversationRepository.save(conversation);

        // 3️⃣ Create group metadata
        ConversationGroup group = new ConversationGroup();
        group.setConversation(conversation);
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setCreatedBy(creatorId);

        groupRepository.save(group);

        // 4️⃣ Add creator
        memberRepository.save(
                new ConversationMember(conversation, creatorId));

        // 5️⃣ Add members
        for (Long userId : members) {
            memberRepository.save(
                    new ConversationMember(conversation, userId));
        }

        return new CreateGroupResponse(
                conversation.getConversationId(),
                ConversationType.GROUP,
                group.getName(),
                conversation.getCreatedTimestamp());
    }
}
