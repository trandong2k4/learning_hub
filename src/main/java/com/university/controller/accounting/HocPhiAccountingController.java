package com.university.controller.accounting;

import com.university.annotation.RequirePermission;
import com.university.dto.request.admin.ThanhToanHocPhiAdminRequestDTO;
import com.university.dto.response.admin.ThanhToanHocPhiAdminResponseDTO;
import com.university.dto.response.accounting.AccountingHocPhiResponse;
import com.university.dto.response.accounting.AccountingInvoiceGenerationResponse;
import com.university.dto.response.accounting.AccountingInvoiceSemesterResponse;
import com.university.dto.response.accounting.AccountingStudentLedgerResponse;
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
@RequestMapping("/api/accountant/hoc-phi")
@RequiredArgsConstructor
@RequirePermission("ACCOUNTANT_TUITION_VIEW")
public class HocPhiAccountingController {

    private final HocPhiAccountingService accountingService;

    @GetMapping("/due")
    public ResponseEntity<List<AccountingHocPhiResponse>> getDueHocPhi() {
        return ResponseEntity.ok(accountingService.getDueHocPhi());
    }

    @GetMapping
    public ResponseEntity<List<AccountingHocPhiResponse>> getAllHocPhi() {
        return ResponseEntity.ok(accountingService.getAllHocPhi());
    }

    @GetMapping("/students/{hocVienId}/ledger")
    public ResponseEntity<AccountingStudentLedgerResponse> getStudentLedger(@PathVariable UUID hocVienId) {
        return ResponseEntity.ok(accountingService.getStudentLedger(hocVienId));
    }

    @GetMapping("/invoices/semesters")
    public ResponseEntity<List<AccountingInvoiceSemesterResponse>> getInvoiceSemesters() {
        return ResponseEntity.ok(accountingService.getInvoiceSemesters());
    }

    @GetMapping("/invoices/preview")
    public ResponseEntity<AccountingInvoiceGenerationResponse> previewInvoices(@RequestParam UUID hocKiId) {
        return ResponseEntity.ok(accountingService.previewInvoices(hocKiId));
    }

    @PostMapping("/invoices/generate")
    @RequirePermission("ACCOUNTANT_TUITION_CREATE")
    public ResponseEntity<AccountingInvoiceGenerationResponse> generateInvoices(@RequestParam UUID hocKiId) {
        return ResponseEntity.ok(accountingService.generateInvoices(hocKiId));
    }

    @PostMapping("/{id}/notify")
    @RequirePermission("ACCOUNTANT_TUITION_NOTIFY")
    public ResponseEntity<Map<String, String>> notifyHocPhi(@PathVariable UUID id) {
        accountingService.sendTuitionNotification(id);
        return ResponseEntity.ok(Map.of("message", "Đã gửi email thông báo"));
    }

    @PostMapping("/notify/bulk")
    @RequirePermission("ACCOUNTANT_TUITION_NOTIFY")
    public ResponseEntity<Map<String, Object>> notifyBulk(@RequestBody List<UUID> ids) {
        int sent = accountingService.sendTuitionNotificationBulk(ids);
        return ResponseEntity.ok(Map.of("requested", ids.size(), "sent", sent));
    }

    @PostMapping("/notify/user/{usersId}")
    @RequirePermission("ACCOUNTANT_TUITION_NOTIFY")
    public ResponseEntity<Map<String, Object>> notifyByUsersId(@PathVariable UUID usersId) {
        int sent = accountingService.sendTuitionNotificationToUser(usersId);
        return ResponseEntity.ok(Map.of("usersId", usersId, "sent", sent));
    }

    @PostMapping("/{id}/payment")
    @RequirePermission("ACCOUNTANT_TUITION_PAY")
    public ResponseEntity<ThanhToanHocPhiAdminResponseDTO> createPayment(
            @PathVariable UUID id,
            @Valid @RequestBody ThanhToanHocPhiAdminRequestDTO request) {
        ThanhToanHocPhiAdminResponseDTO response = accountingService.createPayment(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/confirm")
    @RequirePermission("ACCOUNTANT_TUITION_PAY")
    public ResponseEntity<Void> confirmPayment(@PathVariable UUID id) {
        accountingService.xacNhanThanhToan(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reject")
    @RequirePermission("ACCOUNTANT_TUITION_PAY")
    public ResponseEntity<Void> rejectPayment(@PathVariable UUID id) {
        accountingService.tuChoiThanhToan(id);
        return ResponseEntity.ok().build();
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
