package com.university.service.admin.excel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.university.dto.request.admin.MonHocAdminRequestDTO;
import com.university.dto.response.admin.ExcelImportResult;
import com.university.entity.MonHoc;
import com.university.repository.admin.MonHocAdminRepository;

public class MonHocExcelListener extends AnalysisEventListener<MonHocAdminRequestDTO> {

    private final MonHocAdminRepository monHocAdminRepository;

    private final List<MonHoc> toSave = new ArrayList<>();
    private final List<String> errors = new ArrayList<>();

    private final Set<String> maMonHocInFile = new HashSet<>();

    private final Set<String> maMonHocInDb;

    private static final int BATCH_COUNT = 100; // Tăng để hiệu suất tốt

    private int rowIndex = 1;
    private int successCount = 0;

    public MonHocExcelListener(MonHocAdminRepository monHocAdminRepository) {
        this.monHocAdminRepository = monHocAdminRepository;
        this.maMonHocInDb = new HashSet<>(monHocAdminRepository.findAllMaMonHOc());
    }

    @Override
    public void invoke(MonHocAdminRequestDTO data, AnalysisContext context) {
        rowIndex++;

        if (data == null || data.getMaMonHoc() == null ||
                data.getMaMonHoc().trim().isEmpty()) {
            errors.add("Dòng " + rowIndex + ": Mã trường không được để trống");
            return;
        }

        // Làm sạch dữ liệu
        String maMonHoc = data.getMaMonHoc().trim().toUpperCase(); // Giả sử mã trường viết hoa
        data.setMaMonHoc(maMonHoc);

        if (data.getTenMonHoc() != null) {
            data.setTenMonHoc(data.getTenMonHoc().trim());
        }

        // === KIỂM TRA HỢP LỆ ===
        if (maMonHoc.length() > 10) {
            errors.add("Dòng " + rowIndex + ": Mã môn học tối đa 10 ký tự");
            return;
        }

        if (data.getTenMonHoc() == null || data.getTenMonHoc().trim().isEmpty()) {
            errors.add("Dòng " + rowIndex + ": Tên môn học không được để trống");
            return;
        }

        // Kiểm tra trùng trong cùng file Excel
        if (maMonHocInFile.contains(maMonHoc)) {
            errors.add("Dòng " + rowIndex + ": Mã môn học '" + maMonHoc + "' bị trùng lặp trong file Excel");
            return;
        }
        maMonHocInFile.add(maMonHoc);

        // Kiểm tra tồn tại trong Database (cách này an toàn và rõ ràng)
        if (maMonHocInDb.contains(maMonHoc)) {
            errors.add("Dòng " + rowIndex + ": Mã môn học '" + maMonHoc + "' đã tồn tại trong cơ sở dữ liệu");
            return;
        }

        // Chuyển sang Entity
        MonHoc monHoc = new MonHoc();
        BeanUtils.copyProperties(data, monHoc);
        toSave.add(monHoc);

        // Lưu batch
        if (toSave.size() >= BATCH_COUNT) {
            saveBatch();
        }
    }

    private void saveBatch() {
        if (!toSave.isEmpty()) {
            try {
                monHocAdminRepository.saveAll(toSave);

                // ✔ chỉ tăng khi save OK
                successCount += toSave.size();

                // ✔ update DB cache (tránh duplicate batch sau)
                toSave.forEach(n -> maMonHocInDb.add(n.getMaMonHoc()));

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