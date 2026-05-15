package com.university.controller.admin;

import com.university.annotation.RequirePermission;
import com.university.dto.request.admin.GiangDayAdminRequestDTO;
import com.university.dto.response.admin.GiangDayAdminResponseDTO;
import com.university.service.admin.GiangDayAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/giang-day")
@RequiredArgsConstructor
@RequirePermission("ADMIN_SCHEDULE_MANAGE_VIEW")
public class GiangDayAdminController {

    private final GiangDayAdminService giangDayService;

    @PostMapping
    public ResponseEntity<GiangDayAdminResponseDTO> createGiangDay(
            @Valid @RequestBody GiangDayAdminRequestDTO request) {
        GiangDayAdminResponseDTO response = giangDayService.createGiangDay(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GiangDayAdminResponseDTO> updateGiangDay(
            @PathVariable UUID id,
            @Valid @RequestBody GiangDayAdminRequestDTO request) {
        GiangDayAdminResponseDTO response = giangDayService.updateGiangDay(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GiangDayAdminResponseDTO> getGiangDay(@PathVariable UUID id) {
        GiangDayAdminResponseDTO response = giangDayService.getGiangDayById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<GiangDayAdminResponseDTO>> getAllGiangDay() {
        return ResponseEntity.ok(giangDayService.getAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGiangDay(@PathVariable UUID id) {
        giangDayService.deleteGiangDay(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete-list")
    public ResponseEntity<Void> deleteList(@RequestBody List<UUID> ids) {
        giangDayService.deleteAllByList(ids);
        return ResponseEntity.noContent().build();
    }
}
