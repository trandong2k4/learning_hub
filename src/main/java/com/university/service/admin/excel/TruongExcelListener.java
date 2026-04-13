package com.university.service.admin.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.university.dto.request.admin.TruongAdminRequestDTO;
import com.university.dto.response.admin.ExcelImportResult;
import com.university.entity.Truong;
import com.university.repository.admin.TruongAdminRepository;
import org.springframework.beans.BeanUtils;

import java.util.*;

public class TruongExcelListener extends
        AnalysisEventListener<TruongAdminRequestDTO> {

    private final TruongAdminRepository truongRepository;

    private final List<Truong> toSave = new ArrayList<>();
    private final List<String> errors = new ArrayList<>();

    private final Set<String> maTruongInFile = new HashSet<>(); // Kiểm tra trùng trong file

    private final Set<String> maPermissionsInDb;

    private static final int BATCH_COUNT = 100; // Tăng để hiệu suất tốt

    private int rowIndex = 1;

    public TruongExcelListener(TruongAdminRepository truongRepository) {
        this.truongRepository = truongRepository;
        this.maPermissionsInDb = new HashSet<>(truongRepository.findAllMaTruong());
    }

    @Override
    public void invoke(TruongAdminRequestDTO data, AnalysisContext context) {
        rowIndex++;

        if (data == null || data.getMaTruong() == null ||
                data.getMaTruong().trim().isEmpty()) {
            errors.add("Dòng " + rowIndex + ": Mã trường không được để trống");
            return;
        }

        // Làm sạch dữ liệu
        String maTruong = data.getMaTruong().trim().toUpperCase(); // Giả sử mã trường viết hoa
        data.setMaTruong(maTruong);

        if (data.getTenTruong() != null) {
            data.setTenTruong(data.getTenTruong().trim());
        }

        // === KIỂM TRA HỢP LỆ ===
        if (maTruong.length() > 10) {
            errors.add("Dòng " + rowIndex + ": Mã trường tối đa 10 ký tự");
            return;
        }

        if (data.getTenTruong() == null || data.getTenTruong().trim().isEmpty()) {
            errors.add("Dòng " + rowIndex + ": Tên trường không được để trống");
            return;
        }

        // Kiểm tra trùng trong cùng file Excel
        if (maTruongInFile.contains(maTruong)) {
            errors.add("Dòng " + rowIndex + ": Mã trường '" + maTruong + "' bị trùng lặp trong file Excel");
            return;
        }
        maTruongInFile.add(maTruong);

        // Kiểm tra tồn tại trong Database (cách này an toàn và rõ ràng)
        if (maPermissionsInDb.contains(maTruong)) {
            errors.add("Dòng " + rowIndex + ": Mã trường '" + maTruong + "' đã tồn tại trong cơ sở dữ liệu");
            return;
        }

        // Chuyển sang Entity
        Truong truong = new Truong();
        BeanUtils.copyProperties(data, truong);
        truong.setNgayThanhLap(data.getNgayThanhLap().atStartOfDay());
        toSave.add(truong);

        // Lưu batch
        if (toSave.size() >= BATCH_COUNT) {
            saveBatch();
        }
    }

    private void saveBatch() {
        if (!toSave.isEmpty()) {
            try {
                truongRepository.saveAll(toSave);
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
        result.setSuccessCount(toSave.isEmpty() ? (rowIndex - 1 - errors.size()) : 0); // Nếu còn dữ liệu chưa lưu, coi
        // như chưa thành công
        result.setErrorCount(errors.size());
        result.setErrors(new ArrayList<>(errors));

        return result;
    }
}