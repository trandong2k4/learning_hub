package com.university.controller.student;

import com.university.dto.response.student.LichCaNhanStudentResponse;
import com.university.service.student.LichStudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/student/schedule")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class LichStudentController {

    private final LichStudentService lichStudentService;

    @GetMapping("/me")
    public ResponseEntity<LichCaNhanStudentResponse> getLichCaNhan(
            @RequestParam(defaultValue = "WEEK") String view,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(lichStudentService.getLichCaNhan(view, date));
    }
}
