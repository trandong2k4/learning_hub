package com.university.controller.chatbot;

import com.university.config.SecurityUtils;
import com.university.entity.ChatLog;
import com.university.entity.Users;
import com.university.repository.student.UserRepository;
import com.university.security.CustomUserDetails;
import com.university.service.chatbot.ChatLogService;
import com.university.service.chatbot.SmartChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

        // Pass full CustomUserDetails so SmartChatService can resolve the correct provider
        CustomUserDetails userDetails = SecurityUtils.getCurrentUserDetails();
        Users user = userRepository.findById(userDetails.getUserId()).orElseThrow();

        chatLogService.save(user, "user", message);
        String response = smartChatService.chat(message, userDetails);
        chatLogService.save(user, "bot", response);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<ChatLog>> getHistory() {
        CustomUserDetails userDetails = SecurityUtils.getCurrentUserDetails();
        Users user = userRepository.findById(userDetails.getUserId()).orElseThrow();
        return ResponseEntity.ok(chatLogService.getHistory(user));
    }

    @DeleteMapping("/history")
    public ResponseEntity<Void> clearHistory() {
        CustomUserDetails userDetails = SecurityUtils.getCurrentUserDetails();
        Users user = userRepository.findById(userDetails.getUserId()).orElseThrow();
        chatLogService.clearHistory(user);
        return ResponseEntity.noContent().build();
    }
}
