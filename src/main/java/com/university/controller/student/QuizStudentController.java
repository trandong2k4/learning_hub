package com.university.controller.student;

import com.university.dto.response.student.*;
import com.university.dto.request.student.QuizAnswerSaveItemRequest;
import com.university.dto.request.student.QuizAttemptEventRequest;
import com.university.dto.request.student.QuizAutoSaveRequest;
import com.university.service.student.QuizStudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.university.annotation.RequirePermission;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/student/quiz")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_student')")
@RequirePermission("STU_QUIZ_LIST_VIEW")
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

    // GET /api/student/quiz/attempts/{attemptId}
    @GetMapping("/attempts/{attemptId}")
    public ResponseEntity<QuizAttemptStudentResponse> getAttempt(
            @PathVariable UUID attemptId) {

        return ResponseEntity.ok(quizStudentService.getAttempt(attemptId));
    }

    // PUT /api/student/quiz/{attemptId}/answers
    @PutMapping("/{attemptId}/answers")
    public ResponseEntity<QuizAttemptStudentResponse> autoSaveAnswers(
            @PathVariable UUID attemptId,
            @RequestBody QuizAutoSaveRequest request) {

        return ResponseEntity.ok(quizStudentService.autoSaveAnswers(attemptId, request));
    }

    // POST /api/student/quiz/{attemptId}/events
    @PostMapping("/{attemptId}/events")
    public ResponseEntity<Void> logAttemptEvent(
            @PathVariable UUID attemptId,
            @RequestBody QuizAttemptEventRequest request) {

        quizStudentService.logAttemptEvent(attemptId, request);
        return ResponseEntity.ok().build();
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

    @PostMapping("/{attemptId}/submit-detail")
    public ResponseEntity<QuizResultStudentResponse> submitQuizDetail(
            @PathVariable UUID attemptId,
            @RequestBody List<QuizAnswerSaveItemRequest> answers) {

        return ResponseEntity.ok(quizStudentService.submitQuiz(attemptId, answers));
    }
}
