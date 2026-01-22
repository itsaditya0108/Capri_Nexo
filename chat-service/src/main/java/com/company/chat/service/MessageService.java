package com.company.chat.service;


import com.company.chat.controller.ConversationSseController;
import com.company.chat.dto.MessageResponse;
import com.company.chat.entity.*;
import com.company.chat.repository.ConversationMemberRepository;
import com.company.chat.repository.ConversationRepository;
import com.company.chat.repository.MessageReadRepository;
import com.company.chat.repository.MessageRepository;

import org.springframework.data.domain.PageRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;


import java.util.List;

@Service
public class MessageService {

    private final ConversationRepository conversationRepository;
    private final ConversationMemberRepository memberRepository;
    private final MessageRepository messageRepository;
    private final MessageReadRepository messageReadRepository;
    private final ConversationSseController sseController;


    public MessageService(
            ConversationRepository conversationRepository,
            ConversationMemberRepository memberRepository,
            MessageRepository messageRepository,
            MessageReadRepository messageReadRepository, ConversationSseController sseController
    ) {
        this.conversationRepository = conversationRepository;
        this.memberRepository = memberRepository;
        this.messageRepository = messageRepository;
        this.messageReadRepository = messageReadRepository;
        this.sseController = sseController;
    }

    // ------------------------
    // SEND MESSAGE
    // ------------------------
    @Transactional
    public Message sendMessage(Long conversationId, Long senderId, String content) {

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        memberRepository
                .findByConversation_ConversationIdAndUserId(conversationId, senderId)
                .orElseThrow(() -> new IllegalStateException("User not in conversation"));

        Message message = new Message();
        message.setConversation(conversation);
        message.setSenderId(senderId);
        message.setContent(content);

        Message saved = messageRepository.save(message);

        MessageResponse response = new MessageResponse(
                saved.getMessageId(),
                saved.getConversation().getConversationId(),
                saved.getSenderId(),
                saved.getContent(),
                saved.getCreatedTimestamp()
        );

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        try {
                            sseController.sendMessageEvent(
                                    saved.getConversation().getConversationId(),
                                    response
                            );
                        } catch (Exception e) {
                            // NEVER fail message saving because of SSE
                            // log.warn("SSE failed", e);
                        }
                    }
                }
        );

        return saved;
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
                        conversationId
                );
    }
    // ------------------------
    // MARK AS READ
    // ------------------------
    @Transactional
    public void markAsRead(Long messageId, Long userId) {

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found"));

        Long conversationId = message.getConversation().getConversationId();

        memberRepository
                .findByConversation_ConversationIdAndUserId(conversationId, userId)
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
                                conversationId,
                                messageId,
                                userId
                        );
                    }
                }
        );
    }

    //Pagination Service
    @Transactional(readOnly = true)
    public List<Message> loadMessagesPage(
            Long conversationId,
            Long userId,
            Long beforeMessageId
    ) {
        memberRepository
                .findByConversation_ConversationIdAndUserId(conversationId, userId)
                .orElseThrow(() -> new IllegalStateException("User not in conversation"));

        return messageRepository.findMessagesPage(
                conversationId,
                beforeMessageId,
                PageRequest.of(0, 20)
        );
    }

    @Transactional
    public void markConversationAsRead(Long conversationId, Long userId) {

        memberRepository
                .findByConversation_ConversationIdAndUserId(conversationId, userId)
                .orElseThrow(() -> new IllegalStateException("User not in conversation"));

        List<Message> unreadMessages =
                messageRepository.findUnreadMessages(conversationId, userId);

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
                                    userId
                            );
                        }
                    }
            );
        }
    }


}
