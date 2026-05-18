package com.university.repository.chatbot;

import com.university.entity.ChatLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ChatLogRepository extends JpaRepository<ChatLog, UUID> {
    List<ChatLog> findByUserIdOrderByCreatedAtAsc(UUID userId);

    void deleteByUserId(UUID userId);
}
