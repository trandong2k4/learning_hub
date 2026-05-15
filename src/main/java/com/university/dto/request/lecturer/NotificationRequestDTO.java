package com.university.dto.request.lecturer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class NotificationRequestDTO {
    @NotNull(message = "Mã lớp học phần không được để trống")
    private UUID lopHocPhanId;

    @NotBlank(message = "Tiêu đề thông báo không được để trống")
    private String tieuDe;

    @NotBlank(message = "Nội dung thông báo không được để trống")
    private String noiDung;

    private String fileThongBao;

    public UUID getLopHocPhanId() {
        return lopHocPhanId;
    }

    public void setLopHocPhanId(UUID lopHocPhanId) {
        this.lopHocPhanId = lopHocPhanId;
    }

    public String getTieuDe() {
        return tieuDe;
    }

    public void setTieuDe(String tieuDe) {
        this.tieuDe = tieuDe;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public String getFileThongBao() {
        return fileThongBao;
    }

    public void setFileThongBao(String fileThongBao) {
        this.fileThongBao = fileThongBao;
    }
}
