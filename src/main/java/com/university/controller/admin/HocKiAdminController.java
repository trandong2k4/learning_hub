package com.university.controller.admin;

import com.university.annotation.RequirePermission;
import com.university.dto.request.admin.HocKiAdminRequestDTO;
import com.university.dto.response.admin.HocKiAdminResponseDTO;
import com.university.service.admin.HocKiAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/hoc-ki")
@RequiredArgsConstructor
@RequirePermission("ADMIN_SCHEDULE_MANAGE_VIEW")
public class HocKiAdminController {

    private final HocKiAdminService hocKiService;

    @PostMapping
    public ResponseEntity<HocKiAdminResponseDTO> create(
            @Valid @RequestBody HocKiAdminRequestDTO request) {
        HocKiAdminResponseDTO response = hocKiService.createHocKi(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HocKiAdminResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody HocKiAdminRequestDTO request) {
        HocKiAdminResponseDTO response = hocKiService.updateHocKi(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HocKiAdminResponseDTO> get(@PathVariable UUID id) {
        HocKiAdminResponseDTO response = hocKiService.getHocKiById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<HocKiAdminResponseDTO.HocKiView>> getAllHocKi() {
        return ResponseEntity.ok(hocKiService.getAllHocKi());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        hocKiService.deleteHocKi(id);
        return ResponseEntity.noContent().build();
    }
}
