package com.company.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChatServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatServiceApplication.class, args);

	}

}

//package com.company.chat.chat_service;
//
//import com.company.chat.chat_service.entity.Conversation;
//import com.company.chat.chat_service.entity.ConversationMember;
//import com.company.chat.chat_service.entity.ConversationType;
//import com.company.chat.chat_service.entity.Message;
//import com.company.chat.chat_service.repository.ConversationMemberRepository;
//import com.company.chat.chat_service.repository.ConversationRepository;
//import com.company.chat.chat_service.repository.MessageRepository;
//import com.company.chat.chat_service.service.MessageService;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.annotation.Bean;
//
//@SpringBootApplication
//public class ChatServiceApplication {
//
//    private final ConversationRepository conversationRepository;
//    private final ConversationMemberRepository memberRepository;
//    private final MessageRepository messageRepository;
//    private final MessageService messageService;
//
//    public ChatServiceApplication(
//            ConversationRepository conversationRepository,
//            ConversationMemberRepository memberRepository, MessageRepository messageRepository, MessageService messageService
//    ) {
//        this.conversationRepository = conversationRepository;
//        this.memberRepository = memberRepository;
//        this.messageRepository = messageRepository;
//        this.messageService = messageService;
//    }
//
//    public static void main(String[] args) {
//        SpringApplication.run(ChatServiceApplication.class, args);
//    }
//
//    @Bean
//    CommandLineRunner testMembers() {
//        return args -> {
//            Conversation conv = conversationRepository.findById(1L)
//                    .orElseThrow();
//
//            ConversationMember m1 = new ConversationMember();
//            m1.setConversation(conv);
//            m1.setUserId(1L);
//
//            ConversationMember m2 = new ConversationMember();
//            m2.setConversation(conv);
//            m2.setUserId(2L);
//
//            memberRepository.save(m1);
//            memberRepository.save(m2);
//
//            System.out.println("Members inserted");
//
//
//            Message msg = new Message();
//            msg.setConversation(conv);
//            msg.setSenderId(1L);
//            msg.setContent("Hello from chat service");
//
//            messageRepository.save(msg);
//            messageService.sendMessage(1L, 1L, "Service layer works");
//
//        };
//
//
//
//    }
//}
