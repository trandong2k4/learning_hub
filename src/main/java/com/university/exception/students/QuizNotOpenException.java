package com.university.exception.students;

public class QuizNotOpenException extends RuntimeException {
    public QuizNotOpenException(String message) {
        super(message);
    }
}