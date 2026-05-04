package com.university.service.admin.excel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.university.dto.request.admin.NhanVienAdminRequestDTO;
import com.university.dto.response.admin.ExcelImportResult;
import com.university.entity.NhanVien;
import com.university.repository.admin.NhanVienAdminRepository;
// import com.university.repository.admin.UsersAdminRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NhanVienExcelListener extends AnalysisEventListener<NhanVienAdminRequestDTO> {

    private final NhanVienAdminRepository nhanVienAdminRepository;
    // private final UsersAdminRepository usersAdminRepository;

    private final List<NhanVien> toSave = new ArrayList<>();
    private final List<String> errors = new ArrayList<>();

    private final Set<String> maNhanVienInDb;

    private final Set<String> maNhanVienInFile = new HashSet<>(); // Kiểm tra trùng trong file

    private static final int BATCH_COUNT = 100; // Tăng để hiệu suất tốt

    private int rowIndex = 1;
    private int successCount = 0;

    @Override
    public void invoke(NhanVienAdminRequestDTO data, AnalysisContext context) {
        rowIndex++;

        if (data == null || data.getMaNhanVien() == null ||
                data.getMaNhanVien().trim().isEmpty()) {
            errors.add("Dòng " + rowIndex + ": Mã nhân viên không được để trống");
            return;
        }

        // Làm sạch dữ liệu
        String maNhanVien = data.getMaNhanVien().trim().toUpperCase();

        if (data.getMaNhanVien() != null) {
            data.setMaNhanVien(data.getMaNhanVien().trim());
        }

        // === KIỂM TRA HỢP LỆ ===
        if (maNhanVien.length() > 10) {
            errors.add("Dòng " + rowIndex + ": Mã nhân viên tối đa 10 ký tự");
            return;
        }
        // Kiểm tra trùng trong cùng file Excel
        if (maNhanVienInFile.contains(maNhanVien)) {
            errors.add("Dòng " + rowIndex + ": Mã nhân viên '" + maNhanVien + "' bị trùng lặp trong file Excel");
            return;
        }
        maNhanVienInFile.add(maNhanVien);

        // Kiểm tra tồn tại trong Database (cách này an toàn và rõ ràng)
        if (maNhanVienInDb.contains(maNhanVien)) {
            errors.add("Dòng " + rowIndex + ": Mã nhân viên '" + maNhanVien + "' đã tồn tại trong cơ sở dữ liệu");
            return;
        }

        // Chuyển sang Entity
        NhanVien nhanVien = new NhanVien();
        BeanUtils.copyProperties(data, nhanVien);
        toSave.add(nhanVien);

        // Lưu batch
        if (toSave.size() >= BATCH_COUNT) {
            saveBatch();
        }
    }

    private void saveBatch() {
        if (!toSave.isEmpty()) {
            try {
                nhanVienAdminRepository.saveAll(toSave);

                // ✔ chỉ tăng khi save OK
                successCount += toSave.size();

                // ✔ update DB cache (tránh duplicate batch sau)
                toSave.forEach(n -> maNhanVienInDb.add(n.getMaNhanVien()));

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