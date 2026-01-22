package com.company.chat.controller;

import com.company.chat.dto.MessageResponse;
import com.company.chat.dto.SendMessageRequest;
import com.company.chat.entity.Message;
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

        public MessageController(MessageService messageService, ConversationSseController sseController) {
                this.messageService = messageService;
                this.sseController = sseController;
        }

        // ---------------- SEND MESSAGE ----------------
        @PostMapping("/{conversationId}/messages")
        public MessageResponse sendMessage(
                        @PathVariable Long conversationId,
                        @Valid @RequestBody SendMessageRequest request) {
                Long userId = currentUserId();

                Message message = messageService.sendMessage(
                                conversationId,
                                userId,
                                request.getContent());

                return new MessageResponse(
                                message.getMessageId(),
                                conversationId,
                                message.getSenderId(),
                                message.getContent(),
                                message.getCreatedTimestamp());
        }

        // ---------------- LOAD RECENT ----------------
        @GetMapping("/{conversationId}/messages")
        public List<MessageResponse> loadMessages(
                        @PathVariable Long conversationId) {
                Long userId = currentUserId();

                return messageService
                                .loadRecentMessages(conversationId, userId)
                                .stream()
                                .map(m -> new MessageResponse(
                                                m.getMessageId(),
                                                conversationId,
                                                m.getSenderId(),
                                                m.getContent(),
                                                m.getCreatedTimestamp()))
                                .toList();
        }

        // ---------------- PAGINATION ----------------
        @GetMapping("/{conversationId}/messages/page")
        public List<MessageResponse> loadMessagesPage(
                        @PathVariable Long conversationId,
                        @RequestParam(required = false) Long beforeMessageId) {
                Long userId = currentUserId();

                return messageService
                                .loadMessagesPage(conversationId, userId, beforeMessageId)
                                .stream()
                                .map(m -> new MessageResponse(
                                                m.getMessageId(),
                                                conversationId,
                                                m.getSenderId(),
                                                m.getContent(),
                                                m.getCreatedTimestamp()))
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
        public void typing(@PathVariable Long conversationId) {
                Long userId = currentUserId();

                // username ideally comes from JWT or cached service
                String username = "User " + userId;

                sseController.sendTypingEvent(conversationId, userId, username);
        }

}
