package com.university.service.admin.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.university.dto.response.admin.ExcelImportResult;
import com.university.entity.Phong;
import com.university.enums.TinhTrangPhongEnum;
import com.university.repository.admin.PhongAdminRepository;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

public class PhongExcelListener extends AnalysisEventListener<PhongExcelListener.PhongExcelRow> {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PhongExcelRow {
        @ExcelProperty(index = 0)
        private String maPhong;

        @ExcelProperty(index = 1)
        private String tenPhong;

        @ExcelProperty(index = 2)
        private String toaNha;

        @ExcelProperty(index = 3)
        private String tang;

        @ExcelProperty(index = 4)
        private String sucChua;

        @ExcelProperty(index = 5)
        private String tinhTrang;
    }

    private static final Map<String, TinhTrangPhongEnum> TINH_TRANG_MAP = new HashMap<>();

    static {
        TINH_TRANG_MAP.put("DANG_SU_DUNG", TinhTrangPhongEnum.DANG_SU_DUNG);
        TINH_TRANG_MAP.put("Đang sử dụng", TinhTrangPhongEnum.DANG_SU_DUNG);
        TINH_TRANG_MAP.put("dang su dung", TinhTrangPhongEnum.DANG_SU_DUNG);

        TINH_TRANG_MAP.put("CHUA_SU_DUNG", TinhTrangPhongEnum.CHUA_SU_DUNG);
        TINH_TRANG_MAP.put("Chưa sử dụng", TinhTrangPhongEnum.CHUA_SU_DUNG);
        TINH_TRANG_MAP.put("chua su dung", TinhTrangPhongEnum.CHUA_SU_DUNG);

        TINH_TRANG_MAP.put("DANG_SUA_CHUA", TinhTrangPhongEnum.DANG_SUA_CHUA);
        TINH_TRANG_MAP.put("Đang sửa chữa", TinhTrangPhongEnum.DANG_SUA_CHUA);
        TINH_TRANG_MAP.put("dang sua chua", TinhTrangPhongEnum.DANG_SUA_CHUA);

        TINH_TRANG_MAP.put("KHONG_SU_DUNG_NUA", TinhTrangPhongEnum.KHONG_SU_DUNG_NUA);
        TINH_TRANG_MAP.put("Không sử dụng nữa", TinhTrangPhongEnum.KHONG_SU_DUNG_NUA);
        TINH_TRANG_MAP.put("khong su dung nua", TinhTrangPhongEnum.KHONG_SU_DUNG_NUA);
    }

    private static final int BATCH_COUNT = 100;

    private final PhongAdminRepository phongRepository;
    private final Set<String> maPhongInDb;
    private final Set<String> maPhongInFile = new HashSet<>();
    private final List<Phong> toSave = new ArrayList<>();
    private final List<String> errors = new ArrayList<>();

    private int rowIndex = 1;
    private int successCount = 0;

    public PhongExcelListener(PhongAdminRepository phongRepository) {
        this.phongRepository = phongRepository;
        this.maPhongInDb = new HashSet<>(phongRepository.findAllMaPhong());
    }

    @Override
    public void invoke(PhongExcelRow data, AnalysisContext context) {
        rowIndex++;

        if (data == null || data.getMaPhong() == null || data.getMaPhong().trim().isEmpty()) {
            errors.add("Dòng " + rowIndex + ": Mã phòng không được để trống");
            return;
        }

        String maPhong = data.getMaPhong().trim().toUpperCase();
        String tenPhong = data.getTenPhong() != null ? data.getTenPhong().trim() : null;

        if (maPhong.length() > 10) {
            errors.add("Dòng " + rowIndex + ": Mã phòng '" + maPhong + "' tối đa 10 ký tự");
            return;
        }

        if (tenPhong == null || tenPhong.isEmpty()) {
            errors.add("Dòng " + rowIndex + ": Tên phòng không được để trống (mã: " + maPhong + ")");
            return;
        }

        if (tenPhong.length() > 30) {
            errors.add("Dòng " + rowIndex + ": Tên phòng '" + tenPhong + "' tối đa 30 ký tự");
            return;
        }

        if (maPhongInFile.contains(maPhong)) {
            errors.add("Dòng " + rowIndex + ": Mã phòng '" + maPhong + "' bị trùng lặp trong file Excel");
            return;
        }
        maPhongInFile.add(maPhong);

        if (maPhongInDb.contains(maPhong)) {
            errors.add("Dòng " + rowIndex + ": Mã phòng '" + maPhong + "' đã tồn tại trong cơ sở dữ liệu");
            return;
        }

        // Parse optional fields
        Integer tang = null;
        if (data.getTang() != null && !data.getTang().trim().isEmpty()) {
            try {
                tang = Integer.parseInt(data.getTang().trim());
            } catch (NumberFormatException e) {
                errors.add("Dòng " + rowIndex + ": Tầng '" + data.getTang() + "' không hợp lệ (phải là số nguyên)");
                return;
            }
        }

        Integer sucChua = null;
        if (data.getSucChua() != null && !data.getSucChua().trim().isEmpty()) {
            try {
                sucChua = Integer.parseInt(data.getSucChua().trim());
                if (sucChua <= 0) {
                    errors.add("Dòng " + rowIndex + ": Sức chứa phải lớn hơn 0");
                    return;
                }
            } catch (NumberFormatException e) {
                errors.add("Dòng " + rowIndex + ": Sức chứa '" + data.getSucChua() + "' không hợp lệ (phải là số nguyên)");
                return;
            }
        }

        TinhTrangPhongEnum tinhTrang = null;
        if (data.getTinhTrang() != null && !data.getTinhTrang().trim().isEmpty()) {
            tinhTrang = TINH_TRANG_MAP.get(data.getTinhTrang().trim());
            if (tinhTrang == null) {
                errors.add("Dòng " + rowIndex + ": Tình trạng '" + data.getTinhTrang() + "' không hợp lệ. Giá trị cho phép: DANG_SU_DUNG, CHUA_SU_DUNG, DANG_SUA_CHUA, KHONG_SU_DUNG_NUA");
                return;
            }
        }

        Phong phong = new Phong();
        phong.setMaPhong(maPhong);
        phong.setTenPhong(tenPhong);
        phong.setToaNha(data.getToaNha() != null ? data.getToaNha().trim() : null);
        phong.setTang(tang);
        phong.setSucChua(sucChua);
        phong.setTinhTrang(tinhTrang);

        toSave.add(phong);

        if (toSave.size() >= BATCH_COUNT) {
            saveBatch();
        }
    }

    private void saveBatch() {
        if (!toSave.isEmpty()) {
            try {
                phongRepository.saveAll(toSave);
                successCount += toSave.size();
                toSave.forEach(p -> maPhongInDb.add(p.getMaPhong()));
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
