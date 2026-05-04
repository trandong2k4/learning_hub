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

import java.util.List;
import java.util.UUID;

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

    public void delete(UUID permissionsId) {
        if (r.existsByPermissionsId(permissionsId)) {
            throw new SimpleMessageException(
                    "Permissions đang gán cho Role trong bảng RolePermissions, không thể xóa");
        }
        permissionsAdminRepository.deleteById(permissionsId);
    }

    @Transactional
    public void deleteAllByList(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        try {
            // Kiem tra user dang co trong cac db khac khong
            // for (UUID uuid : ids) {
            // if (usersAdminRepository.) {

            // }
            // }
            permissionsAdminRepository.deleteAllByIdIn(ids);

        } catch (Exception e) {
            throw new SimpleMessageException("Lỗi khi xóa danh sách: " + e.getMessage());
        }
    }
}