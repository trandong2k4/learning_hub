package com.university.service.admin.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.university.dto.request.admin.NganhAdminRequestDTO;
import com.university.dto.response.admin.ExcelImportResult;
import com.university.entity.Khoa;
import com.university.entity.Nganh;
import com.university.repository.admin.KhoaAdminRepository;
import com.university.repository.admin.NganhAdminRepository;

import org.springframework.beans.BeanUtils;

import java.util.*;

public class NganhExcelListener extends
        AnalysisEventListener<NganhAdminRequestDTO> {

    private final NganhAdminRepository nganhAdminRepository;

    private final List<Nganh> toSave = new ArrayList<>();
    private final List<String> errors = new ArrayList<>();

    private final Set<String> maNganhInFile = new HashSet<>(); // Kiểm tra trùng trong file

    private final Set<String> maNganhInDb; // Kiểm tra trùng trong db

    private final Map<String, Khoa> khoaMap;

    private static final int BATCH_COUNT = 100; // Tăng để hiệu suất tốt

    private int rowIndex = 1;
    private int successCount = 0;

    public NganhExcelListener(KhoaAdminRepository khoaAdminRepository, NganhAdminRepository nganhAdminRepository) {
        this.nganhAdminRepository = nganhAdminRepository;
        this.maNganhInDb = new HashSet<>(nganhAdminRepository.findAllMaNganh());
        this.khoaMap = new HashMap<>();
        khoaAdminRepository.findAll()
                .forEach(k -> khoaMap.put(k.getMaKhoa(), k));
    }

    @Override
    public void invoke(NganhAdminRequestDTO data, AnalysisContext context) {
        rowIndex++;
        if (data == null || data.getMaNganh() == null ||
                data.getMaNganh().trim().isEmpty()) {
            errors.add("Dòng " + rowIndex + ": Mã ngành không được để trống");
            return;
        }

        // Làm sạch dữ liệu
        String maNganh = data.getMaNganh().trim().toUpperCase(); // Giả sử mã trường viết hoa
        data.setMaNganh(maNganh);
        String maKhoa = data.getMaKhoa().trim().toUpperCase(); // Giả sử mã trường viết hoa
        data.setMaKhoa(maKhoa);
        // === KIỂM TRA HỢP LỆ ===
        if (maNganh.length() > 10) {
            errors.add("Dòng " + rowIndex + ": Mã ngành tối đa 10 ký tự");
            return;
        }

        if (data.getTenNganh() == null || data.getTenNganh().trim().isEmpty()) {
            errors.add("Dòng " + rowIndex + ": Tên ngành không được để trống");
            return;
        }

        if (data.getMaKhoa() == null || data.getMaKhoa().trim().isEmpty()) {
            errors.add("Dòng " + rowIndex + ": ID khoa không được để trống");
            return;
        }
        Khoa khoa = khoaMap.get(data.getMaKhoa());
        if (khoa == null) {
            errors.add("Dòng " + rowIndex + ": Khoa không tồn tại");
            return;
        }

        // Kiểm tra trùng trong cùng file Excel
        if (maNganhInFile.contains(maNganh)) {
            errors.add("Dòng " + rowIndex + ": Mã ngành '" + maNganh + "' bị trùng lặp trong file Excel");
            return;
        }
        maNganhInFile.add(maNganh);

        // Kiểm tra tồn tại trong Database (cách này an toàn và rõ ràng)
        if (maNganhInDb.contains(maNganh)) {
            errors.add("Dòng " + rowIndex + ": Mã ngành '" + maNganh + "' đã tồn tại trong cơ sở dữ liệu");
            return;
        }

        data.setTenNganh(data.getTenNganh().trim());

        if (data.getMoTa() != null) {
            data.setMoTa(data.getMoTa().trim());
        }

        // Chuyển sang Entity
        Nganh nganh = new Nganh();
        BeanUtils.copyProperties(data, nganh);
        nganh.setKhoa(khoa);
        toSave.add(nganh);

        // Lưu batch
        if (toSave.size() >= BATCH_COUNT) {
            saveBatch();
        }
    }

    private void saveBatch() {
        if (!toSave.isEmpty()) {
            try {
                System.out.println("Heellaa");
                nganhAdminRepository.saveAll(toSave);
                successCount += toSave.size();
                // ✔ update DB cache (tránh duplicate batch sau)
                toSave.forEach(n -> maNganhInDb.add(n.getMaNganh()));

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
        result.setTotalRows(rowIndex - 1);
        result.setSuccessCount(successCount);
        // như chưa thành công
        result.setErrorCount(errors.size());
        result.setErrors(new ArrayList<>(errors));

        return result;
    }
}