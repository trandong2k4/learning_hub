package com.university.mapper.student;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import com.university.dto.response.student.*;
import com.university.entity.Answers;
import com.university.entity.Questions;
import com.university.entity.Quiz;
import com.university.entity.QuizAttempt;
import com.university.entity.QuizQuestions;
import com.university.enums.QuizStatusEnum;


@Component
public class QuizStudentMapper {

    // ================= QUIZ LIST =================
    // 👉 Dùng cho API: GET /quiz
    // 👉 Chỉ lấy thông tin cơ bản (KHÔNG lấy question)
    public QuizListStudentResponse toQuizListDTO(Quiz quiz, QuizStatusEnum status) {
        return toQuizListDTO(quiz, status, null);
    }

    public QuizListStudentResponse toQuizListDTO(Quiz quiz, QuizStatusEnum status, QuizAttempt attempt) {
        if (quiz == null)
            return null;

        QuizListStudentResponse dto = new QuizListStudentResponse();

        dto.setId(quiz.getId());
        dto.setTieuDe(quiz.getTieuDe());
        dto.setThoiGianBatDau(quiz.getThoiGianBatDau());
        dto.setThoiGianKetThuc(quiz.getThoiGianKetThuc());
        dto.setThoiGianLam(quiz.getThoiGianLam());
        dto.setStatus(status);
        if (attempt != null && Boolean.TRUE.equals(attempt.getStatus())) {
            dto.setScore(attempt.getScore());
        }

        // ⚠ GỢI Ý:
        // Có thể thêm:
        // - status (OPEN / CLOSED / UPCOMING)
        // - số câu hỏi (quiz.getDQuizExercises().size())

        return dto;
    }

    public List<QuizListStudentResponse> toQuizListDTOs(List<Quiz> quizzes, QuizStatusEnum status) {
        if (quizzes == null)
            return List.of();

        return quizzes.stream()
                .map(quiz -> toQuizListDTO(quiz, status))
                .toList();
    }

    // ================= ANSWER =================
    // 👉 KHÔNG trả isCorrect (tránh lộ đáp án)
    public AnswerStudentResponse toAnswerDTO(Answers answer) {
        if (answer == null)
            return null;

        AnswerStudentResponse dto = new AnswerStudentResponse();

        dto.setId(answer.getId());
        dto.setContent(answer.getConText());

        // ❌ KHÔNG:
        // dto.setIsCorrect(answer.getIsCorrect());

        return dto;
    }

    // ================= QUESTION =================
    // 👉 1 question có nhiều answer
    public QuestionStudentResponse toQuestionDTO(Questions question) {
        if (question == null)
            return null;

        QuestionStudentResponse dto = new QuestionStudentResponse();

        dto.setId(question.getId());
        dto.setContent(question.getNoiDung());

        if (question.getDAnswers() != null) {
            dto.setAnswers(
                    question.getDAnswers()
                            .stream()
                            .map(this::toAnswerDTO)
                            .toList());
        }

        // ⚠ GỢI Ý:
        // Có thể shuffle đáp án:
        // Collections.shuffle(dto.getAnswers());

        return dto;
    }

    // ================= QUIZ DETAIL =================
    // 👉 Dùng cho: GET /quiz/{id}
    // 👉 Mapping sâu nhất (dễ lỗi nhất)
    public QuizDetailStudentResponse toQuizDetailDTO(Quiz quiz, Integer remainingTime) {
        if (quiz == null)
            return null;

        QuizDetailStudentResponse dto = new QuizDetailStudentResponse();

        dto.setId(quiz.getId());
        dto.setTieuDe(quiz.getTieuDe());
        dto.setMoTa(quiz.getMoTa());
        dto.setThoiGianBatDau(quiz.getThoiGianBatDau());
        dto.setThoiGianKetThuc(quiz.getThoiGianKetThuc());
        dto.setThoiGianLam(quiz.getThoiGianLam());
        dto.setRemainingTime(remainingTime);

        dto.setQuestions(getQuestions(quiz).stream()
                .map(this::toQuestionDTO)
                .toList());
        // ⚠ GỢI Ý:
        // Có thể shuffle câu hỏi:
        // Collections.shuffle(dto.getQuestions());

        return dto;
    }

    // ================= START QUIZ =================
    // 👉 Sau khi user bấm "Bắt đầu"
    public QuizStartStudentResponse toQuizStartDTO(QuizAttempt attempt) {
        if (attempt == null)
            return null;

        QuizStartStudentResponse dto = new QuizStartStudentResponse();

        dto.setAttemptId(attempt.getId());
        dto.setRemainingTime(attempt.getRemainingTime());
        dto.setStartTime(attempt.getStartTime());
        
        // ⚠ GỢI Ý:
        // remainingTime = quiz.getThoiGianLam() * 60 (nếu tính bằng giây)

        return dto;
    }

    // ================= RESULT =================
    // 👉 Sau khi submit
    public QuizResultStudentResponse toQuizResultDTO(
            QuizAttempt attempt,
            int correct,
            int total) {
        if (attempt == null)
            return null;

        QuizResultStudentResponse dto = new QuizResultStudentResponse();

        dto.setAttemptId(attempt.getId());
        dto.setScore(attempt.getScore());
        dto.setCorrect(correct);
        dto.setTotal(total);
        return dto;
    }

    private List<Questions> getQuestions(Quiz quiz) {
        Map<java.util.UUID, Questions> questions = new LinkedHashMap<>();

        if (quiz.getDQuizQuestions() != null) {
            quiz.getDQuizQuestions().stream()
                    .map(QuizQuestions::getQuestions)
                    .filter(q -> q != null && q.getId() != null)
                    .forEach(q -> questions.putIfAbsent(q.getId(), q));
        }

        if (quiz.getDQuizExercises() != null) {
            quiz.getDQuizExercises().stream()
                    .filter(qe -> qe.getExercise() != null && qe.getExercise().getDQuestions() != null)
                    .flatMap(qe -> qe.getExercise().getDQuestions().stream())
                    .filter(q -> q != null && q.getId() != null)
                    .forEach(q -> questions.putIfAbsent(q.getId(), q));
        }

        return List.copyOf(questions.values());
    }
}
