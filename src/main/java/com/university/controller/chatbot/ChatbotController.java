package com.university.controller.chatbot;

import com.university.config.SecurityUtils;
import com.university.entity.ChatLog;
import com.university.entity.Users;
import com.university.repository.student.UserRepository;
import com.university.service.chatbot.ChatLogService;
import com.university.service.chatbot.SmartChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final SmartChatService smartChatService;
    private final ChatLogService chatLogService;
    private final UserRepository userRepository;

    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody Map<String, String> body) {
        String message = body.get("message");
        UUID userId = SecurityUtils.getCurrentUserId();
        Users user = userRepository.findById(userId).orElseThrow();

        chatLogService.save(user, "user", message);
        String response = smartChatService.chat(message, userId);
        chatLogService.save(user, "bot", response);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<ChatLog>> getHistory() {
        UUID userId = SecurityUtils.getCurrentUserId();
        Users user = userRepository.findById(userId).orElseThrow();
        return ResponseEntity.ok(chatLogService.getHistory(user));
    }
}
