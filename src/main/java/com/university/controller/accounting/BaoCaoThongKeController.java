package com.university.controller.accounting;

import com.university.dto.response.accounting.BaoCaoThongKeOverviewResponse;
import com.university.dto.response.accounting.PaymentInfoResponse;
import com.university.service.accounting.BaoCaoThongKeService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/accounting")
@RequiredArgsConstructor
public class BaoCaoThongKeController {

    private final BaoCaoThongKeService baoCaoThongKeService;

    @GetMapping("/report/overview")
    public ResponseEntity<BaoCaoThongKeOverviewResponse> getOverview(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(baoCaoThongKeService.getOverview(start, end));
    }

    @GetMapping("/report/payments")
    public ResponseEntity<List<PaymentInfoResponse>> getPayments(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(baoCaoThongKeService.getPayments(start, end));
    }

}
