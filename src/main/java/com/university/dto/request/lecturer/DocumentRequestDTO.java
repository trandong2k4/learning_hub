package com.university.dto.request.lecturer;

import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DocumentRequestDTO {
    @NotNull(message = "Mã lớp học phần không được để trống")
    private UUID lopHocPhanId;

    @NotBlank(message = "Tên tài liệu không được để trống")
    private String tenTaiLieu;

    private String moTa;

    @NotBlank(message = "URL tài liệu không được để trống")
    private String fileTaiLieuUrl;

    private String loaiTaiLieu;

    public UUID getLopHocPhanId() {
        return lopHocPhanId;
    }

    public void setLopHocPhanId(UUID lopHocPhanId) {
        this.lopHocPhanId = lopHocPhanId;
    }

    public String getTenTaiLieu() {
        return tenTaiLieu;
    }

    public void setTenTaiLieu(String tenTaiLieu) {
        this.tenTaiLieu = tenTaiLieu;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getFileTaiLieuUrl() {
        return fileTaiLieuUrl;
    }

    public void setFileTaiLieuUrl(String fileTaiLieuUrl) {
        this.fileTaiLieuUrl = fileTaiLieuUrl;
    }

    public String getLoaiTaiLieu() {
        return loaiTaiLieu;
    }

    public void setLoaiTaiLieu(String loaiTaiLieu) {
        this.loaiTaiLieu = loaiTaiLieu;
    }
}
