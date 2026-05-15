package com.university.service.admin;

import com.alibaba.excel.EasyExcel;
import com.university.dto.request.admin.PermissionsAdminRequestDTO;
import com.university.dto.response.admin.ExcelImportResult;
import com.university.dto.response.admin.PermissionsAdminResponseDTO;
import com.university.entity.Permissions;
import com.university.exception.SimpleMessageException;
import com.university.mapper.admin.PermissionsAdminMapper;
import com.university.repository.admin.PermissionsAdminRepository;
import com.university.repository.admin.RolePermissionsAdminRepository;
import com.university.service.admin.excel.PermissionsExcelListener;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionsAdminService {

    private final PermissionsAdminRepository permissionsAdminRepository;
    private final PermissionsAdminMapper permissionsAdminMapper;

    public ExcelImportResult importFromExcel(MultipartFile file) throws java.io.IOException {
        PermissionsExcelListener listener = new PermissionsExcelListener(permissionsAdminRepository);

        EasyExcel.read(file.getInputStream(), PermissionsAdminRequestDTO.class,
                listener)
                .sheet("Permissions")
                .headRowNumber(1)
                .doRead();

        return listener.getResult();
    }

    public PermissionsAdminResponseDTO create(PermissionsAdminRequestDTO dto) {
        if (StringUtils.isBlank(dto.getMaPermissions())) {
            throw new SimpleMessageException("Mã quyền không được để trống");
        }

        if (StringUtils.isBlank(dto.getMoTa())) {
            throw new SimpleMessageException("Tên quyền không được để trống");
        }

        if (permissionsAdminRepository.existsByMaPermissions(dto.getMaPermissions()))
            throw new SimpleMessageException("Mã quyền '" + dto.getMaPermissions() + "' đã tồn tại!");

        try {
            Permissions permissions = permissionsAdminMapper.toEntity(dto);
            return permissionsAdminMapper.toResponseDTO(permissionsAdminRepository.save(permissions));

        } catch (Exception e) {
            throw new SimpleMessageException("Thêm quyền không thành công!");
        }
    }

    public List<PermissionsAdminResponseDTO> getAll() {
        return permissionsAdminRepository.getAllPermissionsDTO();
    }

    public Permissions getPermissionsById(UUID id) {
        Permissions permissions = permissionsAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy quyền"));
        return permissions;
    }

    public List<PermissionsAdminResponseDTO> getByMaPermissions(String keyword) {
        return permissionsAdminRepository.findPermissionsByMaPermissions(keyword);
    }

    public PermissionsAdminResponseDTO update(UUID id, PermissionsAdminRequestDTO dto) {
        Permissions permissions = getPermissionsById(id);
        if (StringUtils.isBlank(dto.getMaPermissions())) {
            throw new SimpleMessageException("Mã quyèn không được để trống");
        } else if (StringUtils.isBlank(dto.getMoTa())) {
            throw new SimpleMessageException("Tên quyền không được để trống");
        }
        permissionsAdminMapper.upDateEntity(permissions, dto);
        return permissionsAdminMapper.toResponseDTO(permissions);
    }

    private final RolePermissionsAdminRepository r;

    private void checkCanDelete(UUID permissionsId) {
        if (r.existsByPermissionsId(permissionsId)) {
            throw new SimpleMessageException("Quyền đang được gán cho vai trò, không thể xóa");
        }
    }

    public void delete(UUID permissionsId) {
        checkCanDelete(permissionsId);
        permissionsAdminRepository.deleteById(permissionsId);
    }

    @Transactional
    public List<String> deleteAllByList(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return new java.util.ArrayList<>();
        }

        Set<UUID> assignedIds = r.findAssignedPermissionsIds(ids);
        Map<UUID, String> permissionCodes = permissionsAdminRepository.findPermissionCodesByIds(ids).stream()
                .collect(Collectors.toMap(row -> (UUID) row[0], row -> (String) row[1]));

        List<UUID> deletable = ids.stream()
                .filter(id -> !assignedIds.contains(id))
                .toList();
        List<String> cannotDelete = assignedIds.stream()
                .map(id -> permissionCodes.getOrDefault(id, "Unknown"))
                .toList();

        if (!deletable.isEmpty()) {
            permissionsAdminRepository.deleteAllByIdIn(deletable);
        }

        return cannotDelete;
    }
}
