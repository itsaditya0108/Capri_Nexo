package com.company.chat.service;

import com.company.chat.client.AuthUserClient;
import com.company.chat.dto.CreateGroupRequest;
import com.company.chat.dto.CreateGroupResponse;
import com.company.chat.dto.GroupDetailsResponse;
import com.company.chat.dto.UserSummary;
import com.company.chat.entity.Conversation;
import com.company.chat.entity.ConversationGroup;
import com.company.chat.entity.ConversationMember;
import com.company.chat.entity.ConversationType;
import com.company.chat.repository.ConversationGroupRepository;
import com.company.chat.repository.ConversationMemberRepository;
import com.company.chat.repository.ConversationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GroupConversationService {

    private final ConversationRepository conversationRepository;
    private final ConversationGroupRepository groupRepository;
    private final ConversationMemberRepository memberRepository;
    private final AuthUserClient authUserClient;

    public GroupConversationService(
            ConversationRepository conversationRepository,
            ConversationGroupRepository groupRepository,
            ConversationMemberRepository memberRepository,
            AuthUserClient authUserClient) {

        this.conversationRepository = conversationRepository;
        this.groupRepository = groupRepository;
        this.memberRepository = memberRepository;
        this.authUserClient = authUserClient;
    }

    /* ================= CREATE GROUP ================= */

    @Transactional
    public CreateGroupResponse createGroup(
            Long creatorId,
            CreateGroupRequest request) {

        Set<Long> members = new HashSet<>(request.getMemberIds());
        members.remove(creatorId);

        if (members.isEmpty()) {
            throw new IllegalArgumentException(
                    "Group must have at least 2 members including creator");
        }

        // 1️⃣ Create conversation
        Conversation conversation = new Conversation();
        conversation.setType(ConversationType.GROUP);
        conversation.setCreatedBy(creatorId);
        conversationRepository.save(conversation);

        // 2️⃣ Create group metadata
        ConversationGroup group = new ConversationGroup();
        group.setConversation(conversation); // ✅ REQUIRED
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setCreatedBy(creatorId);

        groupRepository.save(group);


        // 3️⃣ Add creator
        memberRepository.save(
                new ConversationMember(conversation, creatorId));

        // 4️⃣ Add members
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

    /* ================= GROUP DETAILS ================= */

    @Transactional(readOnly = true)
    public GroupDetailsResponse getDetails(
            Long conversationId,
            Long userId,
            String authHeader) {

        // 1️⃣ Validate membership
        memberRepository
                .findByConversation_ConversationIdAndUserId(conversationId, userId)
                .orElseThrow(() ->
                        new RuntimeException("Not a group member"));

        // 2️⃣ Load group
        ConversationGroup group = groupRepository
                .findById(conversationId)
                .orElseThrow(() ->
                        new RuntimeException("Group not found"));

        // 3️⃣ Load members
        List<ConversationMember> members =
                memberRepository.findByConversation_ConversationId(conversationId);

        Set<Long> memberIds = members.stream()
                .map(ConversationMember::getUserId)
                .collect(Collectors.toSet());

        // 4️⃣ Resolve names
        Map<Long, String> namesById =
                authUserClient.getUserNamesByIds(memberIds, authHeader);

        // 5️⃣ Build response
        List<UserSummary> summaries = members.stream()
                .map(m -> new UserSummary(
                        m.getUserId(),
                        namesById.getOrDefault(m.getUserId(), "Unknown")
                ))
                .toList();

        boolean isAdmin = group.getCreatedBy().equals(userId);

        return new GroupDetailsResponse(
                conversationId,
                group.getName(),
                summaries,
                isAdmin
        );
    }

    @Transactional
    public void addMember(
            Long conversationId,
            Long adminUserId,
            Long newUserId) {

        // 1️⃣ Validate admin
        ConversationGroup group = groupRepository
                .findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (!group.getCreatedBy().equals(adminUserId)) {
            throw new RuntimeException("Only admin can add members");
        }

        // 2️⃣ Prevent duplicates
        boolean exists = memberRepository
                .findByConversation_ConversationIdAndUserId(
                        conversationId, newUserId)
                .isPresent();

        if (exists) {
            throw new RuntimeException("User already in group");
        }

        // 3️⃣ Add member
        Conversation conversation = conversationRepository
                .findById(conversationId)
                .orElseThrow();

        memberRepository.save(
                new ConversationMember(conversation, newUserId)
        );
    }

}
