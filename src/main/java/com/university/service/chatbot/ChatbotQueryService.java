package com.university.service.chatbot;

import com.university.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Dispatcher: selects the appropriate {@link ChatContextProvider} for the
 * authenticated user and delegates context-building to it.
 *
 * Providers are registered as Spring beans and injected as a list; their
 * {@code @Order} annotation controls priority when a user holds multiple roles.
 * Default priority: Student(1) → Lecturer(2) → Accountant(3) → Admin(4).
 */
@Service
@RequiredArgsConstructor
public class ChatbotQueryService {

    /** Spring auto-collects all ChatContextProvider beans in @Order order. */
    private final List<ChatContextProvider> providers;

    /**
     * Returns the first provider whose {@code supports()} returns true,
     * or {@code null} if no provider matches (unknown role).
     */
    public ChatContextProvider resolveProvider(CustomUserDetails userDetails) {
        return providers.stream()
                .filter(p -> p.supports(userDetails))
                .findFirst()
                .orElse(null);
    }

    /**
     * Builds the context string for the given user + message + intent.
     * Returns an empty string if no provider matches.
     */
    public String buildContext(CustomUserDetails userDetails, String message, ChatIntent intent) {
        ChatContextProvider provider = resolveProvider(userDetails);
        if (provider == null) return "";
        return provider.buildContext(userDetails, message, intent);
    }
}
