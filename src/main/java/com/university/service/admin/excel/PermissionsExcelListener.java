package com.university.service.admin.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.university.dto.request.admin.PermissionsAdminRequestDTO;
import com.university.dto.response.admin.ExcelImportResult;
import com.university.entity.Permissions;
import com.university.repository.admin.PermissionsAdminRepository;

import org.springframework.beans.BeanUtils;

import java.util.*;

public class PermissionsExcelListener extends
        AnalysisEventListener<PermissionsAdminRequestDTO> {

    private final PermissionsAdminRepository permissionsRepository;

    private final List<Permissions> toSave = new ArrayList<>();
    private final List<String> errors = new ArrayList<>();

    private final Set<String> maPermiissionsInFile = new HashSet<>(); // Kiểm tra trùng trong file

    private static final int BATCH_COUNT = 100; // Tăng để hiệu suất tốt

    private int rowIndex = 1;

    private final Set<String> maPermissionsInDb;

    public PermissionsExcelListener(PermissionsAdminRepository permissionsRepository) {
        this.permissionsRepository = permissionsRepository;
        this.maPermissionsInDb = new HashSet<>(permissionsRepository.findAllMaPermissions());
    }

    @Override
    public void invoke(PermissionsAdminRequestDTO data, AnalysisContext context) {
        rowIndex++;

        if (data == null || data.getMaPermissions() == null ||
                data.getMaPermissions().trim().isEmpty()) {
            errors.add("Dòng " + rowIndex + ": Mã Permissions không được để trống");
            return;
        }

        // Làm sạch dữ liệu
        String maPermiissions = data.getMaPermissions().trim().toUpperCase(); // Giả sử mã trường viết hoa
        data.setMaPermissions(maPermiissions);

        // === KIỂM TRA HỢP LỆ ===
        if (maPermiissions.length() > 30) {
            errors.add("Dòng " + rowIndex + ": Mã Permissions tối đa 30 ký tự");
            return;
        }

        // Kiểm tra trùng trong cùng file Excel
        if (maPermiissionsInFile.contains(maPermiissions)) {
            errors.add("Dòng " + rowIndex + ": Mã Permissions '" + maPermiissions + "' bị trùng lặp trong file Excel");
            return;
        }
        maPermiissionsInFile.add(maPermiissions);

        // Kiểm tra tồn tại trong Database (cách này an toàn và rõ ràng)
        if (maPermissionsInDb.contains(maPermiissions)) {
            errors.add("Dòng " + rowIndex + ": Mã Permissions '" + maPermiissions + "' đã tồn tại trong cơ sở dữ liệu");
            return;
        }

        // Chuyển sang Entity
        Permissions permissions = new Permissions();
        BeanUtils.copyProperties(data, permissions);
        toSave.add(permissions);

        // Lưu batch
        if (toSave.size() >= BATCH_COUNT) {
            saveBatch();
        }
    }

    private void saveBatch() {
        if (!toSave.isEmpty()) {
            try {
                permissionsRepository.saveAll(toSave);
                maPermissionsInDb.addAll(
                        toSave.stream().map(Permissions::getMaPermissions).toList());
            } catch (Exception e) {
                errors.add("Lỗi khi lưu batch: " + e.getMessage());
            } finally {
                toSave.clear();
            }
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveBatch(); // Lưu nốt phần còn lại
    }

    // Phương thức trả về kết quả import
    public ExcelImportResult getResult() {
        ExcelImportResult result = new ExcelImportResult();
        result.setTotalRows(rowIndex - 1); // Trừ đi header
        result.setSuccessCount(toSave.isEmpty() ? (rowIndex - 1 - errors.size()) : 0); // Nếu còn dữ liệu chưa lưu, coi
        // như chưa thành công
        result.setErrorCount(errors.size());
        result.setErrors(new ArrayList<>(errors));

        return result;
    }
}