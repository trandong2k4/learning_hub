package com.university.service.admin.excel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.university.dto.request.admin.GioHocAdminRequestDTO;
import com.university.dto.response.admin.ExcelImportResult;
import com.university.entity.GioHoc;
import com.university.repository.admin.GioHocAdminRepository;

public class GioHocExcelListener extends AnalysisEventListener<GioHocAdminRequestDTO> {

    private final GioHocAdminRepository gioHocAdminRepository;
    private final List<GioHoc> toSave = new ArrayList<>();
    private final List<String> errors = new ArrayList<>();
    private final Set<String> maGioHocInFile = new HashSet<>();
    private final Set<String> maGioHocInDb;
    private static final int BATCH_COUNT = 100;
    private int rowIndex = 1;
    private int successCount = 0;

    public GioHocExcelListener(GioHocAdminRepository gioHocAdminRepository) {
        this.gioHocAdminRepository = gioHocAdminRepository;
        this.maGioHocInDb = new HashSet<>(gioHocAdminRepository.findAllMaGioHoc());
    }

    @Override
    public void invoke(GioHocAdminRequestDTO data, AnalysisContext context) {
        rowIndex++;

        if (data == null || data.getMaGioHoc() == null || data.getMaGioHoc().trim().isEmpty()) {
            errors.add("Dòng " + rowIndex + ": Mã giờ học không được để trống");
            return;
        }

        String maGioHoc = data.getMaGioHoc().trim().toUpperCase();

        if (data.getTenGioHoc() != null) {
            data.setTenGioHoc(data.getTenGioHoc().trim());
        }

        if (maGioHoc.length() > 10) {
            errors.add("Dòng " + rowIndex + ": Mã giờ học tối đa 10 ký tự");
            return;
        }

        if (data.getTenGioHoc() == null || data.getTenGioHoc().trim().isEmpty()) {
            errors.add("Dòng " + rowIndex + ": Tên giờ học không được để trống");
            return;
        }

        if (maGioHocInFile.contains(maGioHoc)) {
            errors.add("Dòng " + rowIndex + ": Mã giờ học '" + maGioHoc + "' bị trùng lặp trong file Excel");
            return;
        }
        maGioHocInFile.add(maGioHoc);

        if (maGioHocInDb.contains(maGioHoc)) {
            errors.add("Dòng " + rowIndex + ": Mã giờ học '" + maGioHoc + "' đã tồn tại trong cơ sở dữ liệu");
            return;
        }

        GioHoc gioHoc = new GioHoc();
        gioHoc.setMaGioHoc(maGioHoc);
        gioHoc.setTenGioHoc(data.getTenGioHoc());
        gioHoc.setThoiGianBatDau(data.getThoiGianBatDau());
        gioHoc.setThoiGianKetThuc(data.getThoiGianKetThuc());

        toSave.add(gioHoc);

        if (toSave.size() >= BATCH_COUNT) {
            saveBatch();
        }
    }

    private void saveBatch() {
        if (!toSave.isEmpty()) {
            try {
                gioHocAdminRepository.saveAll(toSave);
                successCount += toSave.size();
                toSave.forEach(n -> maGioHocInDb.add(n.getMaGioHoc()));
            } catch (Exception e) {
                errors.add("Lỗi khi lưu batch: " + e.getMessage());
            } finally {
                toSave.clear();
            }
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveBatch();
    }

    public ExcelImportResult getResult() {
        ExcelImportResult result = new ExcelImportResult();
        result.setTotalRows(rowIndex - 1);
        result.setSuccessCount(successCount);
        result.setErrorCount(errors.size());
        result.setErrors(new ArrayList<>(errors));
        return result;
    }
}
