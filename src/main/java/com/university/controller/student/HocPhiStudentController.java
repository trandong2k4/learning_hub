package com.university.controller.student;

import com.university.dto.request.student.ThanhToanHocPhiStudentRequest;
import com.university.dto.response.student.HocPhiTongQuanStudentResponse;
import com.university.dto.response.student.LichSuThanhToanHocPhiStudentResponse;
import com.university.dto.response.student.PhuongThucThanhToanStudentResponse;
import com.university.dto.response.student.ThanhToanHocPhiStudentResponse;
import com.university.service.student.HocPhiStudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/student/hoc-phi")
@RequiredArgsConstructor
public class HocPhiStudentController {

    private final HocPhiStudentService hocPhiStudentService;

    @GetMapping("/summary")
    public ResponseEntity<HocPhiTongQuanStudentResponse> getTongQuanHocPhi() {
        return ResponseEntity.ok(hocPhiStudentService.getTongQuanHocPhi());
    }

    @GetMapping("/methods")
    public ResponseEntity<List<PhuongThucThanhToanStudentResponse>> getDanhSachPhuongThucThanhToan() {
        return ResponseEntity.ok(hocPhiStudentService.getDanhSachPhuongThucThanhToan());
    }

    @PostMapping("/pay")
    public ResponseEntity<ThanhToanHocPhiStudentResponse> thanhToanOnline(
            @Valid @RequestBody ThanhToanHocPhiStudentRequest request) {
        return ResponseEntity.ok(hocPhiStudentService.thanhToanOnline(request));
    }

    @GetMapping("/history")
    public ResponseEntity<LichSuThanhToanHocPhiStudentResponse> getLichSuThanhToan() {
        return ResponseEntity.ok(hocPhiStudentService.getLichSuThanhToan());
    }

    @GetMapping("/{hocPhiId}/invoice")
    public ResponseEntity<byte[]> taiHoaDon(@PathVariable UUID hocPhiId) {
        return hocPhiStudentService.taiHoaDon(hocPhiId);
    }

    @GetMapping("/{hocPhiId}/receipt")
    public ResponseEntity<byte[]> taiBienLai(@PathVariable UUID hocPhiId) {
        return hocPhiStudentService.taiBienLai(hocPhiId);
    }
}
