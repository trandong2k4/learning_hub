package com.university.service.student;

import com.university.dto.request.student.QuizAnswerSaveItemRequest;
import com.university.dto.request.student.QuizAttemptEventRequest;
import com.university.dto.request.student.QuizAutoSaveRequest;
import com.university.dto.response.student.QuizAttemptStudentResponse;
import com.university.dto.response.student.QuizDetailStudentResponse;
import com.university.dto.response.student.QuizListStudentResponse;
import com.university.dto.response.student.QuizResultStudentResponse;
import com.university.dto.response.student.QuizStartStudentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface QuizStudentService {
    
    // 📌 Danh sách quiz
    Page<QuizListStudentResponse> getQuizList(UUID lopHocPhanId, Pageable pageable);

    // 📌 Quiz đang mở
    List<QuizListStudentResponse> getQuizDangMo(UUID lopHocPhanId);

    // 📌 Chi tiết quiz
    QuizDetailStudentResponse getQuizDetail(UUID quizId);

    // 📌 Tìm kiếm quiz
    Page<QuizListStudentResponse> searchQuiz(UUID lopHocPhanId, String keyword, Pageable pageable);

    // 📌 Bắt đầu làm bài
    QuizStartStudentResponse startQuiz(UUID quizId);

    QuizAttemptStudentResponse getAttempt(UUID attemptId);

    QuizAttemptStudentResponse autoSaveAnswers(UUID attemptId, QuizAutoSaveRequest request);

    void logAttemptEvent(UUID attemptId, QuizAttemptEventRequest request);

    // 📌 Nộp bài
    QuizResultStudentResponse submitQuiz(UUID attemptId, Map<UUID, UUID> answers);

    QuizResultStudentResponse submitQuiz(UUID attemptId, List<QuizAnswerSaveItemRequest> answers);
}
