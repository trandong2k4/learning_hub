package com.university.controller.lecturer;

import com.university.annotation.RequirePermission;
import com.university.dto.request.lecturer.QuizRequestDTO;
import com.university.dto.response.lecturer.QuizAttemptDetailResponseDTO;
import com.university.dto.response.lecturer.QuizResponseDTO;
import com.university.dto.response.lecturer.QuizResultResponseDTO;
import com.university.service.lecturer.LecturerQuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lecturer")
@CrossOrigin
@RequiredArgsConstructor
@RequirePermission("LECTURER_ASSESSMENT")
public class LecturerQuizController {

    private final LecturerQuizService quizService;

    @PostMapping("/quiz")
    @PreAuthorize("@permissionService.hasPermission(#userId, 'LECTURER_ASSESSMENT')")
    public ResponseEntity<QuizResponseDTO> createQuiz(
            @RequestParam UUID userId,
            @Valid @RequestBody QuizRequestDTO request) {
        return ResponseEntity.ok(quizService.createQuiz(request, userId));
    }

    @GetMapping("/quiz/{quizId}")
    @PreAuthorize("@permissionService.hasPermission(#userId, 'LECTURER_ASSESSMENT')")
    public ResponseEntity<QuizResponseDTO> getQuiz(
            @PathVariable UUID quizId,
            @RequestParam UUID userId) {
        return ResponseEntity.ok(quizService.getQuiz(quizId, userId));
    }

    @PutMapping("/quiz/{quizId}")
    @PreAuthorize("@permissionService.hasPermission(#userId, 'LECTURER_ASSESSMENT')")
    public ResponseEntity<QuizResponseDTO> updateQuiz(
            @PathVariable UUID quizId,
            @RequestParam UUID userId,
            @Valid @RequestBody QuizRequestDTO request) {
        return ResponseEntity.ok(quizService.updateQuiz(quizId, request, userId));
    }

    @DeleteMapping("/quiz/{quizId}")
    @PreAuthorize("@permissionService.hasPermission(#userId, 'LECTURER_ASSESSMENT')")
    public ResponseEntity<Void> deleteQuiz(
            @PathVariable UUID quizId,
            @RequestParam UUID userId) {
        quizService.deleteQuiz(quizId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/quiz/lop-hoc-phan/{lopHocPhanId}")
    @PreAuthorize("@permissionService.hasPermission(#userId, 'LECTURER_ASSESSMENT')")
    public ResponseEntity<List<QuizResponseDTO>> getQuizzesByLopHocPhan(
            @PathVariable UUID lopHocPhanId,
            @RequestParam UUID userId) {
        return ResponseEntity.ok(quizService.getQuizzesByLopHocPhan(lopHocPhanId, userId));
    }

    @GetMapping("/quiz/{lopHocPhanId}/results/{quizId}")
    @PreAuthorize("@permissionService.hasPermission(#userId, 'LECTURER_ASSESSMENT')")
    public ResponseEntity<QuizResultResponseDTO> getQuizResults(
            @PathVariable UUID lopHocPhanId,
            @PathVariable UUID quizId,
            @RequestParam UUID userId) {
        return ResponseEntity.ok(quizService.getQuizResults(lopHocPhanId, quizId, userId));
    }

    @GetMapping("/quiz/{quizId}/attempts/{hocVienId}/detail")
    @PreAuthorize("@permissionService.hasPermission(#userId, 'LECTURER_ASSESSMENT')")
    public ResponseEntity<QuizAttemptDetailResponseDTO> getQuizAttemptDetail(
            @PathVariable UUID quizId,
            @PathVariable UUID hocVienId,
            @RequestParam UUID userId) {
        return ResponseEntity.ok(quizService.getQuizAttemptDetail(quizId, hocVienId, userId));
    }

    @PutMapping("/quiz/{quizId}/grade/{hocVienId}")
    @PreAuthorize("@permissionService.hasPermission(#userId, 'LECTURER_ASSESSMENT')")
    public ResponseEntity<Void> gradeQuizAttempt(
            @PathVariable UUID quizId,
            @PathVariable UUID hocVienId,
            @RequestParam UUID userId,
            @RequestParam(required = false) Float diem,
            @RequestParam(required = false) String nhanXet) {
        quizService.gradeQuizAttempt(quizId, hocVienId, userId, diem, nhanXet);
        return ResponseEntity.ok().build();
    }
}
