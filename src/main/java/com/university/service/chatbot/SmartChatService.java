package com.university.service.chatbot;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class SmartChatService {

    private final OpenAiChatModel model;
    private final ChatbotQueryService queryService;

    // Bounded LRU map: keeps at most 100 per-user memories
    private final Map<UUID, ChatMemory> userMemories = Collections.synchronizedMap(
            new LinkedHashMap<>(128, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<UUID, ChatMemory> eldest) {
                    return size() > 100;
                }
            }
    );

    private static final String SYSTEM_PROMPT = """
            Bạn là trợ lý học vụ thông minh của hệ thống quản lý đại học (LearningHub).
            Khi được cung cấp dữ liệu hệ thống, hãy sử dụng dữ liệu đó để trả lời chính xác.
            Nếu không có dữ liệu cụ thể, hãy nói thật và hướng dẫn sinh viên tra cứu qua hệ thống.
            Không bịa thông tin. Trả lời ngắn gọn, dễ hiểu, lịch sự bằng tiếng Việt.
            """;

    public SmartChatService(
            @Value("${groq.api.key}") String apiKey,
            @Value("${groq.api.url}") String apiUrl,
            ChatbotQueryService queryService) {

        this.model = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(apiUrl)
                .modelName("llama-3.3-70b-versatile")
                .temperature(0.7)
                .build();
        this.queryService = queryService;
    }

    public String chat(String message, UUID userId) {
        ChatMemory memory = userMemories.computeIfAbsent(userId, id -> buildMemory());

        ChatAssistant assistant = AiServices.builder(ChatAssistant.class)
                .chatLanguageModel(model)
                .chatMemory(memory)
                .build();

        ChatIntent intent = IntentDetector.detect(message);
        String context = queryService.buildContext(userId, intent);

        String augmented = context.isBlank()
                ? message
                : "[Dữ liệu hệ thống - dùng để trả lời, không hiển thị nguyên văn]\n"
                  + context
                  + "\n[Câu hỏi của sinh viên]\n"
                  + message;

        return assistant.reply(augmented);
    }

    private ChatMemory buildMemory() {
        var mem = MessageWindowChatMemory.withMaxMessages(10);
        mem.add(SystemMessage.from(SYSTEM_PROMPT));
        return mem;
    }

    interface ChatAssistant {
        String reply(String message);
    }
}
