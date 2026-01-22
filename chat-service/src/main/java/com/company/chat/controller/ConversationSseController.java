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

    // conversationId -> emitters
    private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public ConversationSseController(
            JwtUtil jwtUtil,
            ConversationMemberRepository memberRepository) {
        this.jwtUtil = jwtUtil;
        this.memberRepository = memberRepository;
    }

    // ================= SUBSCRIBE =================
    @GetMapping(
            value = "/conversations/{conversationId}",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public SseEmitter subscribe(
            @PathVariable Long conversationId,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();

        memberRepository
                .findByConversation_ConversationIdAndUserId(conversationId, userId)
                .orElseThrow(() -> new RuntimeException("Not a member"));

        // 3️⃣ Create emitter
        SseEmitter emitter = new SseEmitter(0L); // no timeout

        emitters
                .computeIfAbsent(conversationId, id -> new CopyOnWriteArrayList<>())
                .add(emitter);

        // 4️⃣ Cleanup
        emitter.onCompletion(() -> emitters.getOrDefault(conversationId, List.of()).remove(emitter));
        emitter.onTimeout(() -> emitters.getOrDefault(conversationId, List.of()).remove(emitter));

        return emitter;
    }

    // ================= PUSH EVENT =================
    public void sendMessageEvent(Long conversationId, MessageResponse message) {
        List<SseEmitter> list = emitters.get(conversationId);
        if (list == null)
            return;

        for (SseEmitter emitter : list) {
            try {
                emitter.send(
                        SseEmitter.event()
                                .name("message")
                                .data(message));
            } catch (Exception e) {
                emitter.complete();
                list.remove(emitter);
            }
        }
    }

    public void sendTypingEvent(Long conversationId, Long userId, String username) {
        List<SseEmitter> list = emitters.get(conversationId);
        if (list == null)
            return;

        for (SseEmitter emitter : list) {
            try {
                emitter.send(
                        SseEmitter.event()
                                .name("typing")
                                .data(Map.of(
                                        "userId", userId,
                                        "username", username)));
            } catch (Exception e) {
                emitter.complete();
                list.remove(emitter);
            }
        }
    }

    public void sendReadReceipt(
            Long conversationId,
            Long messageId,
            Long readerUserId) {
        List<SseEmitter> list = emitters.get(conversationId);
        if (list == null)
            return;

        for (SseEmitter emitter : list) {
            try {
                emitter.send(
                        SseEmitter.event()
                                .name("read")
                                .data(
                                        Map.of(
                                                "messageId", messageId,
                                                "readerUserId", readerUserId)));
            } catch (Exception e) {
                emitter.complete();
                list.remove(emitter);
            }
        }
    }

}