package com.university.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.university.dto.request.admin.KhoaAdminRequestDTO;
import com.university.dto.response.admin.KhoaAdminResponseDTO;
import com.university.entity.Khoa;
import com.university.repository.admin.KhoaAdminRepository;
import com.university.service.admin.KhoaAdminService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/khoa")
@RequiredArgsConstructor
public class KhoaAdminController {

    private final KhoaAdminRepository khoaAdminRepository;
    private final KhoaAdminService khoaService;

    @PostMapping
    public ResponseEntity<KhoaAdminResponseDTO> createKhoa(@RequestBody @Valid KhoaAdminRequestDTO dto)
            throws BadRequestException {
        return ResponseEntity.status(HttpStatus.CREATED).body(khoaService.createKhoa(dto));
    }

    @PostMapping("list")
    public ResponseEntity<List<KhoaAdminResponseDTO>> createList(@RequestBody @Valid List<KhoaAdminRequestDTO> dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(khoaService.createListKhoa(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<KhoaAdminResponseDTO.KhoaView> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(khoaService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<KhoaAdminResponseDTO.KhoaView>> getAll() {
        return ResponseEntity.ok(khoaService.getAllKhoaView());
    }

    @GetMapping("/search")
    public ResponseEntity<List<KhoaAdminResponseDTO>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(khoaService.search(keyword));
    }

    @PutMapping("/{id}")
    public ResponseEntity<KhoaAdminResponseDTO> update(@PathVariable UUID id,
            @RequestBody @Valid KhoaAdminRequestDTO dto) {
        return ResponseEntity.ok(khoaService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        khoaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/by-list")
    public ResponseEntity<String> deleteList(@RequestBody List<UUID> ids) {
        khoaService.deleteAllByList(ids);
        return ResponseEntity.ok("Xóa thành công " + ids.size() + " khoa");
    }

    @GetMapping("/all-id")
    public ResponseEntity<List<UUID>> getAllIds() {
        List<UUID> ids = khoaAdminRepository.findAll()
                .stream()
                .map(Khoa::getId) // Giả sử model của bạn là Khoa
                .toList();

        return ResponseEntity.ok(ids);
    }
}