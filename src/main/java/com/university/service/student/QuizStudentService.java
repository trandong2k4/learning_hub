package com.university.service.student;

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

    // 📌 Nộp bài
    QuizResultStudentResponse submitQuiz(UUID attemptId, Map<UUID, UUID> answers);
}

