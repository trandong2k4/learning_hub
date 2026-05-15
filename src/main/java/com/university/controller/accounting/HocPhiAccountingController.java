package com.university.controller.accounting;

import com.university.dto.request.admin.ThanhToanHocPhiAdminRequestDTO;
import com.university.dto.response.admin.ThanhToanHocPhiAdminResponseDTO;
import com.university.dto.response.accounting.AccountingHocPhiResponse;
import com.university.entity.HocPhi;
import com.university.service.accounting.HocPhiAccountingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounting/hoc-phi")
@RequiredArgsConstructor
public class HocPhiAccountingController {

    private final HocPhiAccountingService accountingService;

    @GetMapping("/due")
    public ResponseEntity<List<AccountingHocPhiResponse>> getDueHocPhi() {
        return ResponseEntity.ok(accountingService.getDueHocPhi());
    }

    @PostMapping("/{id}/notify")
    public ResponseEntity<Map<String, String>> notifyHocPhi(@PathVariable UUID id) {
        accountingService.sendTuitionNotification(id);
        return ResponseEntity.ok(Map.of("message", "Đã gửi email thông báo"));
    }

    @PostMapping("/notify/bulk")
    public ResponseEntity<Map<String, Object>> notifyBulk(@RequestBody List<UUID> ids) {
        int sent = accountingService.sendTuitionNotificationBulk(ids);
        return ResponseEntity.ok(Map.of("requested", ids.size(), "sent", sent));
    }

    @PostMapping("/notify/user/{usersId}")
    public ResponseEntity<Map<String, Object>> notifyByUsersId(@PathVariable UUID usersId) {
        int sent = accountingService.sendTuitionNotificationToUser(usersId);
        return ResponseEntity.ok(Map.of("usersId", usersId, "sent", sent));
    }

    @PostMapping("/{id}/payment")
    public ResponseEntity<ThanhToanHocPhiAdminResponseDTO> createPayment(
            @PathVariable UUID id,
            @Valid @RequestBody ThanhToanHocPhiAdminRequestDTO request) {
        ThanhToanHocPhiAdminResponseDTO response = accountingService.createPayment(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify-token")
    public ResponseEntity<AccountingHocPhiResponse> verifyToken(@RequestParam String token) {
        HocPhi hp = accountingService.verifyToken(token);
        return ResponseEntity.ok(accountingService.getDueHocPhi().stream()
                .filter(d -> d.getId().equals(hp.getId()))
                .findFirst()
                .orElseGet(() -> AccountingHocPhiResponse.builder()
                        .id(hp.getId())
                        .soTien(hp.getSoTien())
                        .trangThai(hp.getTrangThai())
                        .hocVienId(hp.getHocVien() != null ? hp.getHocVien().getId() : null)
                        .hocVienName(hp.getHocVien() != null && hp.getHocVien().getUsers() != null
                                ? hp.getHocVien().getUsers().getHoTen()
                                : null)
                        .hocVienEmail(hp.getHocVien() != null && hp.getHocVien().getUsers() != null
                                ? hp.getHocVien().getUsers().getEmail()
                                : null)
                        .createdAt(hp.getCreatedAt())
                        .updatedAt(hp.getUpdatedAt())
                        .build()));
    }
}
