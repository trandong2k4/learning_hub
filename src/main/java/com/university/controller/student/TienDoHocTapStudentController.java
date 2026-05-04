package com.university.controller.student;

import com.university.dto.request.student.TienDoHocTapStudentRequestDTO;
import com.university.dto.response.student.TienDoHocTapHocKyStudentResponse;
import com.university.dto.response.student.TienDoHocTapMonHocStudentResponse;
import com.university.dto.response.student.TienDoHocTapStudentResponse;
import com.university.dto.response.student.TienDoHocTapTongQuanStudentResponse;
import com.university.service.student.TienDoHocTapStudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/student/learning-progress")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class TienDoHocTapStudentController {

    private final TienDoHocTapStudentService tienDoHocTapStudentService;

    @GetMapping("/me")
    public ResponseEntity<TienDoHocTapStudentResponse> getTienDoHocTap(
            @ModelAttribute @Valid TienDoHocTapStudentRequestDTO request) {
        return ResponseEntity.ok(tienDoHocTapStudentService.getTienDoHocTap(request));
    }

    @GetMapping("/summary")
    public ResponseEntity<TienDoHocTapTongQuanStudentResponse> getTongQuanTienDoHocTap() {
        return ResponseEntity.ok(tienDoHocTapStudentService.getTongQuanTienDoHocTap());
    }

    @GetMapping("/courses")
    public ResponseEntity<List<TienDoHocTapMonHocStudentResponse>> getDanhSachMonHoc(
            @ModelAttribute @Valid TienDoHocTapStudentRequestDTO request) {
        return ResponseEntity.ok(tienDoHocTapStudentService.getDanhSachMonHoc(request));
    }

    @GetMapping("/semesters")
    public ResponseEntity<List<TienDoHocTapHocKyStudentResponse>> getTienDoTheoHocKy(
            @ModelAttribute @Valid TienDoHocTapStudentRequestDTO request) {
        return ResponseEntity.ok(tienDoHocTapStudentService.getTienDoTheoHocKy(request));
    }
}