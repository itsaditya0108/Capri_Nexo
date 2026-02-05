package com.company.chat.controller;

import com.company.chat.dto.MessageResponse;
import com.company.chat.dto.SendMessageRequest;
import com.company.chat.entity.Message;
import com.company.chat.model.MessageType;
import com.company.chat.repository.ConversationMemberRepository;
import com.company.chat.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class MessageController {

        private final MessageService messageService;
        private final ConversationSseController sseController;
        private final com.company.chat.client.AuthUserClient authUserClient;
        private final com.company.chat.repository.MessageReadRepository readRepository;

        public MessageController(
                        MessageService messageService,
                        ConversationSseController sseController,
                        com.company.chat.client.AuthUserClient authUserClient,
                        com.company.chat.repository.MessageReadRepository readRepository) {
                this.messageService = messageService;
                this.sseController = sseController;
                this.authUserClient = authUserClient;
                this.readRepository = readRepository;
        }

        // ---------------- SEND MESSAGE ----------------
        @PostMapping("/{conversationId}/messages")
        public MessageResponse sendMessage(
                        @PathVariable Long conversationId,
                        @Valid @RequestBody SendMessageRequest request,
                        @RequestHeader(value = "Authorization", required = false) String authHeader) {
                Long userId = currentUserId();

                Message message = messageService.sendMessage(
                                conversationId,
                                userId,
                                request,
                                authHeader);

                // Resolve sender name (non-blocking)
                String senderName = "User";
                try {
                        var names = authUserClient.getUserNamesByIds(
                                        java.util.Set.of(userId),
                                        authHeader);
                        senderName = names.getOrDefault(userId, "User");
                } catch (Exception ignored) {
                }

                // Build response
                MessageResponse response = new MessageResponse();
                response.setMessageId(message.getMessageId());
                response.setConversationId(conversationId);
                response.setSenderId(message.getSenderId());
                response.setSenderName(senderName);

                response.setMessageType(message.getMessageType());
                response.setContent(message.getContent());
                response.setImageId(message.getImageId());
                response.setCreatedTimestamp(message.getCreatedTimestamp());
                response.setRead(false);

                sseController.sendMessageEvent(conversationId, response);

                // 🔥 IMAGE-SPECIFIC PART
                if (message.getMessageType() == MessageType.IMAGE) {
                        response.setThumbnailUrl(
                                        "/api/images/" + message.getImageId() + "/thumbnail");
                        response.setDownloadUrl(
                                        "/api/images/" + message.getImageId() + "/download");
                }

                return response;
        }

        // ---------------- LOAD RECENT ----------------
        @GetMapping("/{conversationId}/messages")
        public List<MessageResponse> loadMessages(
                        @PathVariable Long conversationId,
                        @RequestHeader(value = "Authorization", required = false) String authHeader) {
                Long userId = currentUserId();
                List<Message> messages = messageService.loadRecentMessages(conversationId, userId);

                // Batch fetch
                java.util.Set<Long> userIds = messages.stream().map(Message::getSenderId)
                                .collect(java.util.stream.Collectors.toSet());
                java.util.Map<Long, String> namesMap = new java.util.HashMap<>();
                try {
                        namesMap = authUserClient.getUserNamesByIds(userIds, authHeader);
                } catch (Exception e) {
                }
                final java.util.Map<Long, String> finalNames = namesMap;

                return messages.stream()
                                .map(m -> {
                                        boolean isRead = false;
                                        if (m.getSenderId().equals(userId)) {
                                                isRead = readRepository.existsByMessage_MessageId(m.getMessageId());
                                        }

                                        String name = finalNames.getOrDefault(
                                                        m.getSenderId(),
                                                        "User " + m.getSenderId());

                                        return toMessageResponse(
                                                        m,
                                                        conversationId,
                                                        userId,
                                                        name,
                                                        isRead);
                                })
                                .toList();

        }

        // ---------------- PAGINATION ----------------
        @GetMapping("/{conversationId}/messages/page")
        public List<MessageResponse> loadMessagesPage(
                        @PathVariable Long conversationId,
                        @RequestParam(required = false) Long beforeMessageId,
                        @RequestHeader(value = "Authorization", required = false) String authHeader) {
                Long userId = currentUserId();

                List<Message> messages = messageService.loadMessagesPage(conversationId, userId, beforeMessageId);

                // Batch fetch
                java.util.Set<Long> userIds = messages.stream().map(Message::getSenderId)
                                .collect(java.util.stream.Collectors.toSet());
                java.util.Map<Long, String> namesMap = new java.util.HashMap<>();
                try {
                        namesMap = authUserClient.getUserNamesByIds(userIds, authHeader);
                } catch (Exception e) {
                }
                final java.util.Map<Long, String> finalNames = namesMap;

                return messages.stream()
                                .map(m -> {
                                        boolean isRead = false;
                                        if (m.getSenderId().equals(userId)) {
                                                isRead = readRepository.existsByMessage_MessageId(m.getMessageId());
                                        }

                                        String name = finalNames.getOrDefault(
                                                        m.getSenderId(),
                                                        "User " + m.getSenderId());

                                        return toMessageResponse(
                                                        m,
                                                        conversationId,
                                                        userId,
                                                        name,
                                                        isRead);
                                })
                                .toList();

        }

        // ---------------- MARK READ ----------------
        @PostMapping("/messages/{messageId}/read")
        public void markAsRead(@PathVariable Long messageId) {
                Long userId = currentUserId();
                messageService.markAsRead(messageId, userId);
        }

        private Long currentUserId() {
                return (Long) SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getPrincipal();
        }

        @PostMapping("/{conversationId}/read")
        public void markConversationAsRead(@PathVariable Long conversationId) {
                Long userId = currentUserId();

                messageService.markConversationAsRead(conversationId, userId);
        }

        @PostMapping("/{conversationId}/typing")
        public void typing(
                        @PathVariable Long conversationId,
                        @RequestHeader("Authorization") String authHeader) {
                Long userId = currentUserId();

                // Fetch real username
                String username = "User";
                try {
                        var names = authUserClient.getUserNamesByIds(java.util.Set.of(userId), authHeader);
                        username = names.getOrDefault(userId, "User");
                } catch (Exception e) {
                        // fallback
                }

                sseController.sendTypingEvent(conversationId, userId, username);
        }

        private MessageResponse toMessageResponse(
                        Message m,
                        Long conversationId,
                        Long userId,
                        String senderName,
                        boolean isRead) {
                MessageResponse res = new MessageResponse();
                res.setMessageId(m.getMessageId());
                res.setConversationId(conversationId);
                res.setSenderId(m.getSenderId());
                res.setSenderName(senderName);
                res.setMessageType(m.getMessageType());
                res.setContent(m.getContent());
                res.setImageId(m.getImageId());
                res.setCreatedTimestamp(m.getCreatedTimestamp());
                res.setRead(isRead);

                if (m.getMessageType() == MessageType.IMAGE) {
                        res.setThumbnailUrl("/api/images/" + m.getImageId() + "/thumbnail");
                        res.setDownloadUrl("/api/images/" + m.getImageId() + "/download");
                }

                return res;
        }

}
