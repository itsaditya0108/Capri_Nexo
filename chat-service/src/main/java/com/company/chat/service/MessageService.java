package com.company.chat.service;

import com.company.chat.controller.ConversationSseController;
import com.company.chat.dto.MessageResponse;
import com.company.chat.dto.SendMessageRequest;
import com.company.chat.entity.*;
import com.company.chat.model.MessageType;
import com.company.chat.repository.ConversationMemberRepository;
import com.company.chat.repository.ConversationRepository;
import com.company.chat.repository.MessageReadRepository;
import com.company.chat.repository.MessageRepository;

import org.springframework.data.domain.PageRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

        private final ConversationRepository conversationRepository;
        private final ConversationMemberRepository memberRepository;
        private final MessageRepository messageRepository;
        private final MessageReadRepository messageReadRepository;
        private final ConversationSseController sseController;
        private final com.company.chat.client.ImageValidationClient imageValidationClient;

        private final com.company.chat.client.AuthUserClient authUserClient;

        public MessageService(
                        ConversationRepository conversationRepository,
                        ConversationMemberRepository memberRepository,
                        MessageRepository messageRepository,
                        MessageReadRepository messageReadRepository,
                        ConversationSseController sseController,
                        com.company.chat.client.ImageValidationClient imageValidationClient,
                        com.company.chat.client.AuthUserClient authUserClient) {
                this.conversationRepository = conversationRepository;
                this.memberRepository = memberRepository;
                this.messageRepository = messageRepository;
                this.messageReadRepository = messageReadRepository;
                this.sseController = sseController;
                this.imageValidationClient = imageValidationClient;
                this.authUserClient = authUserClient;
        }

        // ------------------------
        // SEND MESSAGE
        // ------------------------
        @Transactional
        public Message sendMessage(
                        Long conversationId,
                        Long senderId,
                        SendMessageRequest request,
                        String authHeader) {

                Conversation conversation = conversationRepository.findById(conversationId)
                                .orElseThrow(() -> new RuntimeException("Conversation not found"));

                // ✅ Membership validation
                boolean isMember = memberRepository
                                .findByConversation_ConversationIdAndUserId(conversationId, senderId)
                                .isPresent();

                if (!isMember) {
                        throw new RuntimeException("Not a conversation member");
                }

                Message message = new Message();
                message.setConversation(conversation);
                message.setSenderId(senderId);
                message.setCreatedTimestamp(java.time.Instant.now());

                // ---------------- TEXT MESSAGE ----------------
                if (request.getType() == MessageType.TEXT) {

                        if (request.getContent() == null || request.getContent().isBlank()) {
                                throw new RuntimeException("Text message content required");
                        }

                        message.setMessageType(MessageType.TEXT);
                        message.setContent(request.getContent());
                        message.setImageId(null);
                }

                // ---------------- IMAGE MESSAGE ----------------
                else if (request.getType() == MessageType.IMAGE) {

                        if (request.getImageId() == null) {
                                throw new RuntimeException("ImageId required for image message");
                        }

                        // 🔥 IMPORTANT — IMAGE VALIDATION (Via Client)
                        boolean isValid = imageValidationClient.validateImage(
                                        request.getImageId(),
                                        senderId,
                                        authHeader);

                        if (!isValid) {
                                throw new RuntimeException("Invalid image or access denied");
                        }

                        message.setMessageType(MessageType.IMAGE);
                        message.setImageId(request.getImageId());
                        message.setContent(request.getContent()); // caption optional
                }

                else {
                        throw new RuntimeException("Unsupported message type");
                }

                return messageRepository.save(message);
        }

        // ------------------------
        // LOAD MESSAGES
        // ------------------------
        @Transactional(readOnly = true)
        public List<Message> loadRecentMessages(Long conversationId, Long userId) {

                memberRepository
                                .findByConversation_ConversationIdAndUserId(conversationId, userId)
                                .orElseThrow(() -> new IllegalStateException("User not in conversation"));

                return messageRepository
                                .findTop50ByConversation_ConversationIdAndDeletedFalseOrderByMessageIdDesc(
                                                conversationId);
        }

        // ------------------------
        // MARK AS READ
        // ------------------------
        @Transactional
        public void markAsRead(Long messageId, Long userId) {

                Message message = messageRepository.findById(messageId)
                                .orElseThrow(() -> new IllegalArgumentException("Message not found"));

                memberRepository
                                .findByConversation_ConversationIdAndUserId(
                                                message.getConversation().getConversationId(), userId)
                                .orElseThrow(() -> new IllegalStateException("User not in conversation"));

                // sender should not mark own message
                if (message.getSenderId().equals(userId)) {
                        return;
                }

                if (messageReadRepository
                                .existsByMessage_MessageIdAndUserId(messageId, userId)) {
                        return;
                }

                MessageRead read = new MessageRead();
                read.setMessage(message);
                read.setUserId(userId);

                messageReadRepository.save(read);

                // 🔥 SEND SSE READ RECEIPT (AFTER COMMIT)
                TransactionSynchronizationManager.registerSynchronization(
                                new TransactionSynchronization() {
                                        @Override
                                        public void afterCommit() {
                                                sseController.sendReadReceipt(
                                                                message.getConversation().getConversationId(),
                                                                messageId,
                                                                userId);
                                        }
                                });
        }

        // Pagination Service
        @Transactional(readOnly = true)
        public List<Message> loadMessagesPage(
                        Long conversationId,
                        Long userId,
                        Long beforeMessageId) {
                memberRepository
                                .findByConversation_ConversationIdAndUserId(conversationId, userId)
                                .orElseThrow(() -> new IllegalStateException("User not in conversation"));

                return messageRepository.findMessagesPage(
                                conversationId,
                                beforeMessageId,
                                PageRequest.of(0, 20));
        }

        @Transactional
        public void markConversationAsRead(Long conversationId, Long userId) {

                memberRepository
                                .findByConversation_ConversationIdAndUserId(conversationId, userId)
                                .orElseThrow(() -> new IllegalStateException("User not in conversation"));

                List<Message> unreadMessages = messageRepository.findUnreadMessages(conversationId, userId);

                for (Message msg : unreadMessages) {
                        MessageRead read = new MessageRead();
                        read.setMessage(msg);
                        read.setUserId(userId);
                        messageReadRepository.save(read);

                        Long messageId = msg.getMessageId();

                        TransactionSynchronizationManager.registerSynchronization(
                                        new TransactionSynchronization() {
                                                @Override
                                                public void afterCommit() {
                                                        sseController.sendReadReceipt(
                                                                        conversationId,
                                                                        messageId,
                                                                        userId);
                                                }
                                        });
                }
        }

}
