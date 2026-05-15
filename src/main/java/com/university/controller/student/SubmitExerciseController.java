package com.university.controller.student;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.university.dto.request.student.ExerciseSubmitRequestDTO;
import com.university.service.student.SubmitExerciseService;
import lombok.RequiredArgsConstructor;
import com.university.annotation.RequirePermission;

@RestController
@RequestMapping("/api/student/exercise")
@RequiredArgsConstructor
@RequirePermission("STU_EXERCISE_LIST_VIEW")
public class SubmitExerciseController {

    private final SubmitExerciseService submitService;

    @PostMapping("/submit")
    public ResponseEntity<?> submit(@Valid @RequestBody ExerciseSubmitRequestDTO request) {
        String result = submitService.submit(request);
        return ResponseEntity.ok(result);
    }
}
