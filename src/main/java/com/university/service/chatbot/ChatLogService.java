package com.university.service.chatbot;

import com.university.entity.ChatLog;
import com.university.entity.Users;
import com.university.repository.chatbot.ChatLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatLogService {

    private final ChatLogRepository repo;

    public void save(Users user, String role, String content) {
        repo.save(ChatLog.builder()
                .user(user)
                .role(role)
                .content(content)
                .build());
    }

    public List<ChatLog> getHistory(Users user) {
        return repo.findByUserIdOrderByCreatedAtAsc(user.getId());
    }

    @Transactional
    public void clearHistory(Users user) {
        repo.deleteByUserId(user.getId());
    }
}
