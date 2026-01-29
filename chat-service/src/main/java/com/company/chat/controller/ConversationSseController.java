package com.company.chat.controller;

import com.company.chat.dto.MessageResponse;
import com.company.chat.repository.ConversationMemberRepository;
import com.company.chat.security.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/api/sse")
public class ConversationSseController {

    private final JwtUtil jwtUtil;
    private final ConversationMemberRepository memberRepository;

    // conversationId -> emitters (for active chat display)
    private final Map<Long, List<SseEmitter>> conversationEmitters = new ConcurrentHashMap<>();

    // userId -> emitters (for global notifications like inbox updates)
    private final Map<Long, List<SseEmitter>> userEmitters = new ConcurrentHashMap<>();

    public ConversationSseController(
            JwtUtil jwtUtil,
            ConversationMemberRepository memberRepository) {
        this.jwtUtil = jwtUtil;
        this.memberRepository = memberRepository;
    }

    // ================= SUBSCRIBE TO CONVERSATION =================
    @GetMapping(value = "/conversations/{conversationId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeToConversation(
            @PathVariable Long conversationId,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();

        memberRepository
                .findByConversation_ConversationIdAndUserId(conversationId, userId)
                .orElseThrow(() -> new RuntimeException("Not a member"));

        SseEmitter emitter = new SseEmitter(0L);

        conversationEmitters
                .computeIfAbsent(conversationId, id -> new CopyOnWriteArrayList<>())
                .add(emitter);

        emitter.onCompletion(() -> conversationEmitters.getOrDefault(conversationId, List.of()).remove(emitter));
        emitter.onTimeout(() -> conversationEmitters.getOrDefault(conversationId, List.of()).remove(emitter));

        return emitter;
    }

    // ================= SUBSCRIBE TO GLOBAL USER NOTIFICATIONS =================
    @GetMapping(value = "/notifications", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeToNotifications(
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();

        SseEmitter emitter = new SseEmitter(0L);

        userEmitters
                .computeIfAbsent(userId, id -> new CopyOnWriteArrayList<>())
                .add(emitter);

        emitter.onCompletion(() -> userEmitters.getOrDefault(userId, List.of()).remove(emitter));
        emitter.onTimeout(() -> userEmitters.getOrDefault(userId, List.of()).remove(emitter));

        return emitter;
    }

    // ================= PUSH EVENT =================
    public void sendMessageEvent(Long conversationId, MessageResponse message) {
        // 1. Send to the specific conversation emitters
        List<SseEmitter> conversationList = conversationEmitters.get(conversationId);
        if (conversationList != null) {
            for (SseEmitter emitter : conversationList) {
                try {
                    emitter.send(SseEmitter.event().name("message").data(message));
                } catch (Exception e) {
                    emitter.complete();
                    conversationList.remove(emitter);
                }
            }
        }

        // 2. Send global notification to all members of this conversation
        // This updates their inbox/unread signs
        memberRepository.findByConversation_ConversationId(conversationId)
                .forEach(member -> {
                    List<SseEmitter> userList = userEmitters.get(member.getUserId());
                    if (userList != null) {
                        for (SseEmitter emitter : userList) {
                            try {
                                emitter.send(SseEmitter.event().name("inbox_update").data(Map.of(
                                        "conversationId", conversationId,
                                        "lastMessage", message.getContent(),
                                        "senderId", message.getSenderId())));
                            } catch (Exception e) {
                                emitter.complete();
                                userList.remove(emitter);
                            }
                        }
                    }
                });
    }

    public void sendTypingEvent(Long conversationId, Long userId, String username) {
        List<SseEmitter> list = conversationEmitters.get(conversationId);
        if (list == null)
            return;

        for (SseEmitter emitter : list) {
            try {
                emitter.send(SseEmitter.event().name("typing").data(Map.of(
                        "userId", userId,
                        "username", username,
                        "conversationId", conversationId)));
            } catch (Exception e) {
                emitter.complete();
                list.remove(emitter);
            }
        }
    }

    public void sendReadReceipt(Long conversationId, Long messageId, Long readerUserId) {
        // Send to conversation list
        List<SseEmitter> conversationList = conversationEmitters.get(conversationId);
        if (conversationList != null) {
            for (SseEmitter emitter : conversationList) {
                try {
                    emitter.send(SseEmitter.event().name("read").data(Map.of(
                            "messageId", messageId,
                            "readerUserId", readerUserId)));
                } catch (Exception e) {
                    emitter.complete();
                    conversationList.remove(emitter);
                }
            }
        }

        // Also send notify user list for inbox unread badge updates
        List<SseEmitter> userList = userEmitters.get(readerUserId);
        if (userList != null) {
            for (SseEmitter emitter : userList) {
                try {
                    emitter.send(SseEmitter.event().name("read_update").data(Map.of(
                            "conversationId", conversationId,
                            "messageId", messageId)));
                } catch (Exception e) {
                    emitter.complete();
                    userList.remove(emitter);
                }
            }
        }
    }

}