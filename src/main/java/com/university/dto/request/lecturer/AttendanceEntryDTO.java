package com.university.dto.request.lecturer;

import java.util.UUID;
import jakarta.validation.constraints.NotNull;

public class AttendanceEntryDTO {
    @NotNull(message = "Mã học viên không được để trống")
    private UUID hocVienId;

    @NotNull(message = "Trạng thái điểm danh phải được chọn")
    private String trangThai;

    private String ghiChu;

    public UUID getHocVienId() {
        return hocVienId;
    }

    public void setHocVienId(UUID hocVienId) {
        this.hocVienId = hocVienId;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
}
