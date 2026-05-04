package com.university.service.admin.excel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.university.dto.request.admin.HocVienAdminRequestDTO;
import com.university.dto.response.admin.ExcelImportResult;
import com.university.entity.HocVien;
import com.university.repository.admin.HocVienAdminRepository;
// import com.university.repository.admin.UsersAdminRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HocVienExcelListener extends AnalysisEventListener<HocVienAdminRequestDTO> {

    private final HocVienAdminRepository HocVienAdminRepository;
    // private final UsersAdminRepository usersAdminRepository;

    private final List<HocVien> toSave = new ArrayList<>();
    private final List<String> errors = new ArrayList<>();

    private final Set<String> maHocVienInDb;

    private final Set<String> maHocVienInFile = new HashSet<>(); // Kiểm tra trùng trong file

    private static final int BATCH_COUNT = 100; // Tăng để hiệu suất tốt

    private int rowIndex = 1;
    private int successCount = 0;

    @Override
    public void invoke(HocVienAdminRequestDTO data, AnalysisContext context) {
        rowIndex++;

        if (data == null || data.getMaHocVien() == null ||
                data.getMaHocVien().trim().isEmpty()) {
            errors.add("Dòng " + rowIndex + ": Mã học viên không được để trống");
            return;
        }

        // Làm sạch dữ liệu
        String maHocVien = data.getMaHocVien().trim().toUpperCase();

        if (data.getMaHocVien() != null) {
            data.setMaHocVien(data.getMaHocVien().trim());
        }

        // === KIỂM TRA HỢP LỆ ===
        if (maHocVien.length() > 10) {
            errors.add("Dòng " + rowIndex + ": Mã học viên tối đa 10 ký tự");
            return;
        }
        // Kiểm tra trùng trong cùng file Excel
        if (maHocVienInFile.contains(maHocVien)) {
            errors.add("Dòng " + rowIndex + ": Mã học viên '" + maHocVien + "' bị trùng lặp trong file Excel");
            return;
        }
        maHocVienInFile.add(maHocVien);

        // Kiểm tra tồn tại trong Database (cách này an toàn và rõ ràng)
        if (maHocVienInDb.contains(maHocVien)) {
            errors.add("Dòng " + rowIndex + ": Mã học viên '" + maHocVien + "' đã tồn tại trong cơ sở dữ liệu");
            return;
        }

        // Chuyển sang Entity
        HocVien HocVien = new HocVien();
        BeanUtils.copyProperties(data, HocVien);
        toSave.add(HocVien);

        // Lưu batch
        if (toSave.size() >= BATCH_COUNT) {
            saveBatch();
        }
    }

    private void saveBatch() {
        if (!toSave.isEmpty()) {
            try {
                HocVienAdminRepository.saveAll(toSave);

                // ✔ chỉ tăng khi save OK
                successCount += toSave.size();

                // ✔ update DB cache (tránh duplicate batch sau)
                toSave.forEach(n -> maHocVienInDb.add(n.getMaHocVien()));

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

        // Nếu muốn dùng @Transactional, nên gọi saveAll một lần duy nhất ở đây (tùy
        // thiết kế)
    }

    // Phương thức trả về kết quả import
    public ExcelImportResult getResult() {
        ExcelImportResult result = new ExcelImportResult();
        result.setTotalRows(rowIndex - 1); // Trừ đi header
        result.setSuccessCount(successCount);
        // như chưa thành công
        result.setErrorCount(errors.size());
        result.setErrors(new ArrayList<>(errors));

        return result;
    }
}