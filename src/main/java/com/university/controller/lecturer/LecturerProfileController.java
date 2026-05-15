package com.university.controller.lecturer;

import com.university.annotation.RequirePermission;
import com.university.dto.request.lecturer.ChangePasswordRequestDTO;
import com.university.dto.request.lecturer.LecturerProfileRequestDTO;
import com.university.dto.response.lecturer.LecturerProfileResponseDTO;
import com.university.dto.response.lecturer.LecturerScheduleDTO;
import com.university.service.lecturer.LecturerProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lecturer")
@CrossOrigin
@RequiredArgsConstructor
@RequirePermission("LECTURER_PROFILE")
public class LecturerProfileController {

    private final LecturerProfileService profileService;

    @GetMapping("/profile/{userId}")
    @PreAuthorize("@permissionService.hasPermission(#userId, 'LECTURER_PROFILE')")
    public ResponseEntity<LecturerProfileResponseDTO> getProfile(@PathVariable UUID userId) {
        return ResponseEntity.ok(profileService.getProfile(userId));
    }

    @PutMapping("/profile/{userId}")
    @PreAuthorize("@permissionService.hasPermission(#userId, 'LECTURER_PROFILE')")
    public ResponseEntity<LecturerProfileResponseDTO> updateProfile(
            @PathVariable UUID userId,
            @Valid @RequestBody LecturerProfileRequestDTO request) {
        return ResponseEntity.ok(profileService.updateProfile(userId, request));
    }

    @PostMapping("/profile/{userId}/change-password")
    @PreAuthorize("@permissionService.hasPermission(#userId, 'LECTURER_PROFILE')")
    public ResponseEntity<Void> changePassword(
            @PathVariable UUID userId,
            @Valid @RequestBody ChangePasswordRequestDTO request) {
        profileService.changePassword(userId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/profile/{userId}/schedule")
    @PreAuthorize("@permissionService.hasPermission(#userId, 'LECTURER_TEACHING')")
    public ResponseEntity<List<LecturerScheduleDTO>> getSchedule(
            @PathVariable UUID userId,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to) {
        LocalDate start = from != null ? from : LocalDate.now();
        LocalDate end = to != null ? to : start.plusWeeks(1);
        return ResponseEntity.ok(profileService.getSchedule(userId, start, end));
    }
}
