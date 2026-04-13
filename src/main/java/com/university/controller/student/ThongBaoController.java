package com.university.controller.student;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.university.dto.response.student.ThongBaoResponse;
import com.university.service.student.ThongBaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.university.dto.request.student.ThongBaoRequest;

@RestController
@RequestMapping("/api/student/thongbao")
@RequiredArgsConstructor

public class ThongBaoController {
    private final ThongBaoService thongBaoService;
    
//     @GetMapping("/test")
// public ResponseEntity<List<ThongBaoResponse>> testThongBao() {
//     UUID fakeUserId = UUID.fromString("11111111-1111-1111-1111-111111111111");
//     return ResponseEntity.ok(thongBaoService.getDanhSachThongBao(fakeUserId));
// }
    @GetMapping("/{usersId}")
    public ResponseEntity<List<ThongBaoResponse>> getDanhSachThongBao(@PathVariable UUID usersId){
        return ResponseEntity.ok(thongBaoService.getDanhSachThongBao(usersId));
    }
    @PatchMapping("/da-doc")
    public ResponseEntity<Void> danhDauDaDoc(@RequestBody ThongBaoRequest request) {
        thongBaoService.danhDauDaDoc(request);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/da-doc/tat-ca")
    public ResponseEntity<Void> danhDauTatCaDaDoc(@RequestBody ThongBaoRequest request) {
        thongBaoService.danhDauTatCaDaDoc(request);
        return ResponseEntity.noContent().build();
    }
}
