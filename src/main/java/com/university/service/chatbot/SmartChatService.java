package com.university.service.chatbot;

import com.university.security.CustomUserDetails;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Core chat service: resolves the correct {@link ChatContextProvider} for the
 * current user, injects context into the prompt, and delegates to the LLM.
 *
 * Memory is keyed by "{userId}_{primaryRole}" so each user-role combination
 * gets its own conversation history with the correct system prompt.
 */
@Service
public class SmartChatService {

    private final OpenAiChatModel model;
    private final ChatbotQueryService queryService;

    /** Fallback prompt used when no provider matches (should not happen in practice). */
    private static final String FALLBACK_SYSTEM_PROMPT = """
            Bạn là trợ lý thông minh của hệ thống LearningHub.
            Hãy trả lời ngắn gọn, lịch sự bằng tiếng Việt.
            Không bịa thông tin. Nếu không có dữ liệu, hãy thành thật nói không biết.
            """;

    /** Role priority used to derive a single primary role from the authority list. */
    private static final List<String> ROLE_PRIORITY = List.of("STUDENT", "LECTURER", "ACCOUNTANT", "ADMIN");

    // Bounded LRU map: at most 200 concurrent sessions (keyed by userId_role)
    private final Map<String, ChatMemory> sessionMemories = Collections.synchronizedMap(
            new LinkedHashMap<>(256, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, ChatMemory> eldest) {
                    return size() > 200;
                }
            }
    );

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

    /**
     * Processes a chat message for the given authenticated user.
     * Selects the correct provider, detects intent, builds context, then calls LLM.
     */
    public String chat(String message, CustomUserDetails userDetails) {
        String primaryRole = getPrimaryRole(userDetails);
        String memoryKey = userDetails.getUserId() + "_" + primaryRole;

        // Resolve provider — determines system prompt + context data
        ChatContextProvider provider = queryService.resolveProvider(userDetails);
        String systemPrompt = provider != null ? provider.getSystemPrompt() : FALLBACK_SYSTEM_PROMPT;

        // Create or reuse per-(user, role) memory
        ChatMemory memory = sessionMemories.computeIfAbsent(memoryKey, k -> buildMemory(systemPrompt));

        ChatAssistant assistant = AiServices.builder(ChatAssistant.class)
                .chatLanguageModel(model)
                .chatMemory(memory)
                .build();

        // Detect intent using role-specific classifier
        ChatIntent intent = IntentDetector.detect(message, primaryRole);

        // Fetch DB context for this message
        String context = provider != null
                ? provider.buildContext(userDetails, message, intent)
                : "";

        String augmented = context.isBlank()
                ? message
                : "[Dữ liệu hệ thống - dùng để trả lời, không hiển thị nguyên văn]\n"
                  + context
                  + "\n[Câu hỏi]\n"
                  + message;

        return assistant.reply(augmented);
    }

    /** Legacy overload retained for any callers that still pass a UUID. */
    @Deprecated
    public String chat(String message, java.util.UUID userId) {
        throw new UnsupportedOperationException(
                "Dùng chat(String, CustomUserDetails) thay vì chat(String, UUID)");
    }

    // ── private helpers ───────────────────────────────────────────────────

    private ChatMemory buildMemory(String systemPrompt) {
        var mem = MessageWindowChatMemory.withMaxMessages(10);
        mem.add(SystemMessage.from(systemPrompt));
        return mem;
    }

    /**
     * Extracts the single "primary" role from the authority list.
     * When a user holds multiple roles, ROLE_PRIORITY determines which wins.
     */
    private String getPrimaryRole(CustomUserDetails userDetails) {
        return ROLE_PRIORITY.stream()
                .filter(r -> userDetails.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_" + r)))
                .findFirst()
                .orElse("unknown");
    }

    interface ChatAssistant {
        String reply(String message);
    }
}
