package com.university.controller.accounting;

import com.university.dto.request.accounting.AccountingChangePasswordRequestDTO;
import com.university.dto.request.accounting.AccountingProfileRequestDTO;
import com.university.dto.response.accounting.AccountingProfileResponseDTO;
import com.university.service.accounting.AccountingProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accountant/profile")
@RequiredArgsConstructor
public class AccountingProfileController {

    private final AccountingProfileService profileService;

    @GetMapping
    public ResponseEntity<AccountingProfileResponseDTO> getProfile() {
        return ResponseEntity.ok(profileService.getProfile());
    }

    @PutMapping
    public ResponseEntity<AccountingProfileResponseDTO> updateProfile(
            @Valid @RequestBody AccountingProfileRequestDTO request) {
        return ResponseEntity.ok(profileService.updateProfile(request));
    }

    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody AccountingChangePasswordRequestDTO request) {
        profileService.changePassword(request);
        return ResponseEntity.ok().build();
    }
}