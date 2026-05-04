package com.university.controller.student;

import com.university.dto.response.student.*;
import com.university.service.student.QuizStudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/student/quiz")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class QuizStudentController {

    private final QuizStudentService quizStudentService;

    // 📌 1. DANH SÁCH QUIZ
    // GET /api/student/quiz?lopHocPhanId=xxx&page=0&size=10
    @GetMapping
    public ResponseEntity<Page<QuizListStudentResponse>> getQuizList(
            @RequestParam UUID lopHocPhanId,
            @PageableDefault(size = 10) Pageable pageable) {

        return ResponseEntity.ok(quizStudentService.getQuizList(lopHocPhanId, pageable));
    }

    // 📌 2. QUIZ ĐANG MỞ
    // GET /api/student/quiz/dang-mo?lopHocPhanId=xxx
    @GetMapping("/dang-mo")
    public ResponseEntity<List<QuizListStudentResponse>> getQuizDangMo(
            @RequestParam UUID lopHocPhanId) {

        return ResponseEntity.ok(quizStudentService.getQuizDangMo(lopHocPhanId));
    }

    // 📌 3. CHI TIẾT QUIZ
    // GET /api/student/quiz/{quizId}
    @GetMapping("/{quizId}")
    public ResponseEntity<QuizDetailStudentResponse> getQuizDetail(
            @PathVariable UUID quizId) {

        return ResponseEntity.ok(quizStudentService.getQuizDetail(quizId));
    }

    // 📌 4. TÌM KIẾM QUIZ
    // GET /api/student/quiz/search?lopHocPhanId=xxx&keyword=xxx
    @GetMapping("/search")
    public ResponseEntity<Page<QuizListStudentResponse>> searchQuiz(
            @RequestParam UUID lopHocPhanId,
            @RequestParam(required = false, defaultValue = "") String keyword,
            @PageableDefault(size = 10) Pageable pageable) {

        return ResponseEntity.ok(quizStudentService.searchQuiz(lopHocPhanId, keyword, pageable));
    }

    // 📌 5. BẮT ĐẦU QUIZ
    // POST /api/student/quiz/{quizId}/start
    @PostMapping("/{quizId}/start")
    public ResponseEntity<QuizStartStudentResponse> startQuiz(
            @PathVariable UUID quizId) {

        return ResponseEntity.ok(quizStudentService.startQuiz(quizId));
    }

    // 📌 6. NỘP BÀI
    // POST /api/student/quiz/{attemptId}/submit
    // Body: { "questionId1": "answerId1", "questionId2": "answerId2" }
    @PostMapping("/{attemptId}/submit")
    public ResponseEntity<QuizResultStudentResponse> submitQuiz(
            @PathVariable UUID attemptId,
            @RequestBody Map<UUID, UUID> answers) {

        return ResponseEntity.ok(quizStudentService.submitQuiz(attemptId, answers));
    }
}