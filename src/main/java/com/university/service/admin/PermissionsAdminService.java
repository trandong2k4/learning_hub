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

    private final PermissionsAdminRepository pr;
    private final PermissionsAdminMapper pm;

    public ExcelImportResult importFromExcel(MultipartFile file) throws java.io.IOException {
        PermissionsExcelListener listener = new PermissionsExcelListener(pr);

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

        if (pr.existsByMaPermissions(dto.getMaPermissions()))
            throw new SimpleMessageException("Mã quyền '" + dto.getMaPermissions() + "' đã tồn tại!");

        try {
            Permissions permissions = pm.toEntity(dto);
            return pm.toResponseDTO(pr.save(permissions));

        } catch (Exception e) {
            throw new SimpleMessageException("Thêm quyền không thành công!");
        }
    }

    public List<PermissionsAdminResponseDTO> getAll() {
        return pr.getAllPermissionsDTO();
    }

    public Permissions getPermissionsById(UUID id) {
        Permissions permissions = pr.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy quyền"));
        return permissions;
    }

    public List<PermissionsAdminResponseDTO> getByMaPermissions(String keyword) {
        return pr.findPermissionsByMaPermissions(keyword);
    }

    public PermissionsAdminResponseDTO update(UUID id, PermissionsAdminRequestDTO dto) {
        Permissions permissions = getPermissionsById(id);
        if (StringUtils.isBlank(dto.getMaPermissions())) {
            throw new SimpleMessageException("Mã quyèn không được để trống");
        } else if (StringUtils.isBlank(dto.getMoTa())) {
            throw new SimpleMessageException("Tên quyền không được để trống");
        }
        pm.upDateEntity(permissions, dto);
        return pm.toResponseDTO(permissions);
    }

    private final RolePermissionsAdminRepository r;

    public void delete(UUID permissionsId) {
        if (r.existsByPermissionsId(permissionsId)) {
            throw new SimpleMessageException(
                    "Permissions đang gán cho Role trong bảng RolePermissions, không thể xóa");
        }
        pr.deleteById(permissionsId);
    }

    public void deleteAllByList(List<UUID> ids) {
        for (UUID id : ids) {
            delete(id);
        }
    }

    @Transactional
    public void deleteAllPermissons() {
        pr.deleteAllPermissions();
    }

}