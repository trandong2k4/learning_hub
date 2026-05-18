package com.university.enums;

public enum AttemptActionEnum {
    START,
    RESUME,
    PAUSE,
    TAB_SWITCH,
    SUBMIT,
    TIMEOUT,
    AUTO_SAVE,
    NAVIGATE_TO, // Viewed the question
    SELECT_OPTION, // Chose an answer
    CLEAR_ANSWER, // Deselected an answer
    FLAG_QUESTION // Marked for review
}
