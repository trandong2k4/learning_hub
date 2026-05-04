package com.university.controller.student;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.university.dto.request.student.SubmitExerciseRequestDTO;
import com.university.service.student.SubmitExerciseService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/student/exercise")
@RequiredArgsConstructor
public class SubmitExerciseController {

    private final SubmitExerciseService submitService;

    @PostMapping("/submit")
    public ResponseEntity<?> submit(@RequestBody SubmitExerciseRequestDTO request) {
        String result = submitService.submit(request);
        return ResponseEntity.ok(result);
    }
}