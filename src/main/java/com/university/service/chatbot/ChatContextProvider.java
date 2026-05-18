package com.university.service.chatbot;

import com.university.security.CustomUserDetails;

/**
 * Strategy interface: each role implements its own context-building logic.
 * Register as a Spring bean — ChatbotQueryService auto-discovers all implementations.
 */
public interface ChatContextProvider {

    /** Returns true when this provider handles the given user's primary role. */
    boolean supports(CustomUserDetails userDetails);

    /** Role-specific system prompt injected into the LLM memory at session start. */
    String getSystemPrompt();

    /**
     * Queries the DB for data relevant to this user + detected intent,
     * then formats it as a plain-text context block for the LLM prompt.
     */
    String buildContext(CustomUserDetails userDetails, String message, ChatIntent intent);
}
