package com.university.controller.student;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.university.dto.request.student.ThongBaoRequest;
import com.university.dto.response.student.ThongBaoResponse;
import com.university.service.student.ThongBaoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/student/thongbao")
@RequiredArgsConstructor
public class ThongBaoController {

    private final ThongBaoService thongBaoService;

    @GetMapping
    public ResponseEntity<List<ThongBaoResponse>> getDanhSachThongBao() {
        return ResponseEntity.ok(thongBaoService.getDanhSachThongBao());
    }

    @PatchMapping("/da-doc")
    public ResponseEntity<Void> danhDauDaDoc(@RequestBody ThongBaoRequest request) {
        thongBaoService.danhDauDaDoc(request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/da-doc/tat-ca")
    public ResponseEntity<Void> danhDauTatCaDaDoc() {
        thongBaoService.danhDauTatCaDaDoc();
        return ResponseEntity.noContent().build();
    }
}
