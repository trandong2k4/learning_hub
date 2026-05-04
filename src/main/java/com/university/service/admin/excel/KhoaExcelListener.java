package com.university.service.admin.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.university.dto.request.admin.KhoaAdminRequestDTO;
import com.university.dto.response.admin.ExcelImportResult;
import com.university.entity.Khoa;
import com.university.entity.Truong;
import com.university.repository.admin.KhoaAdminRepository;
import com.university.repository.admin.TruongAdminRepository;
import org.springframework.beans.BeanUtils;

import java.util.*;

public class KhoaExcelListener extends
        AnalysisEventListener<KhoaAdminRequestDTO> {

    private final KhoaAdminRepository khoaAdminRepository;

    private final List<Khoa> toSave = new ArrayList<>();
    private final List<String> errors = new ArrayList<>();

    private final Set<String> maKhoaInFile = new HashSet<>(); // Kiểm tra trùng trong file

    private final Set<String> maKhoaInDb; // Kiểm tra trùng trong db

    private final Map<String, Truong> truongMap;

    private static final int BATCH_COUNT = 100; // Tăng để hiệu suất tốt

    private int rowIndex = 1;
    private int successCount = 0;

    public KhoaExcelListener(TruongAdminRepository truongRepository, KhoaAdminRepository khoaAdminRepository) {
        this.khoaAdminRepository = khoaAdminRepository;
        this.maKhoaInDb = new HashSet<>(khoaAdminRepository.findAllMaKhoa());
        this.truongMap = new HashMap<>();
        truongRepository.findAll()
                .forEach(t -> truongMap.put(t.getMaTruong(), t));
    }

    @Override
    public void invoke(KhoaAdminRequestDTO data, AnalysisContext context) {
        rowIndex++;

        if (data == null || data.getMaKhoa() == null ||
                data.getMaKhoa().trim().isEmpty()) {
            errors.add("Dòng " + rowIndex + ": Mã trường không được để trống");
            return;
        }

        // Làm sạch dữ liệu
        String maKhoa = data.getMaKhoa().trim().toUpperCase(); // Giả sử mã trường viết hoa
        data.setMaKhoa(maKhoa);

        // === KIỂM TRA HỢP LỆ ===
        if (maKhoa.length() > 10) {
            errors.add("Dòng " + rowIndex + ": Mã trường tối đa 10 ký tự");
            return;
        }

        String maTruong = data.getMaTruong().trim().toUpperCase(); // Giả sử mã trường viết hoa
        data.setMaTruong(maTruong);

        if (data.getMaTruong() == null) {
            errors.add("Dòng " + rowIndex + ": ID Trường không được để trống");
            return;
        }

        Truong truong = truongMap.get(data.getMaTruong());
        if (truong == null) {
            errors.add("Dòng " + rowIndex + ": Trường không tồn tại");
            return;
        }

        // Kiểm tra trùng trong cùng file Excel
        if (maKhoaInFile.contains(maKhoa)) {
            errors.add("Dòng " + rowIndex + ": Mã khoa '" + maKhoa + "' bị trùng lặp trong file Excel");
            return;
        }
        maKhoaInFile.add(maKhoa);

        // Kiểm tra tồn tại trong Database (cách này an toàn và rõ ràng)
        if (maKhoaInDb.contains(maKhoa)) {
            errors.add("Dòng " + rowIndex + ": Mã khoa '" + maKhoa + "' đã tồn tại trong cơ sở dữ liệu");
            return;
        }

        // Chuyển sang Entity
        Khoa khoa = new Khoa();
        BeanUtils.copyProperties(data, khoa);
        khoa.setTruong(truong);
        toSave.add(khoa);

        // Lưu batch
        if (toSave.size() >= BATCH_COUNT) {
            saveBatch();
        }
    }

    private void saveBatch() {
        if (!toSave.isEmpty()) {
            try {
                khoaAdminRepository.saveAll(toSave);

                // ✔ chỉ tăng khi save OK
                successCount += toSave.size();

                // ✔ update DB cache (tránh duplicate batch sau)
                toSave.forEach(n -> maKhoaInDb.add(n.getMaKhoa()));

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