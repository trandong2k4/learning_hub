package com.university.dto.request.lecturer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AssignmentRequestDTO {
    @NotNull(message = "Mã lớp học phần không được để trống")
    private UUID lopHocPhanId;

    @NotBlank(message = "Tiêu đề bài tập không được để trống")
    private String tieuDe;

    private String moTa;

    private String fileExerciseUrl;

    private LocalDateTime thoiGianBatDau;

    private LocalDateTime thoiGianKetThuc;

    @Min(value = 1, message = "Giới hạn lần làm phải lớn hơn hoặc bằng 1")
    private Integer gioiHanLanLam;

    private List<QuestionRequestDTO> questions;

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

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getFileExerciseUrl() {
        return fileExerciseUrl;
    }

    public void setFileExerciseUrl(String fileExerciseUrl) {
        this.fileExerciseUrl = fileExerciseUrl;
    }

    public LocalDateTime getThoiGianBatDau() {
        return thoiGianBatDau;
    }

    public void setThoiGianBatDau(LocalDateTime thoiGianBatDau) {
        this.thoiGianBatDau = thoiGianBatDau;
    }

    public LocalDateTime getThoiGianKetThuc() {
        return thoiGianKetThuc;
    }

    public void setThoiGianKetThuc(LocalDateTime thoiGianKetThuc) {
        this.thoiGianKetThuc = thoiGianKetThuc;
    }

    public Integer getGioiHanLanLam() {
        return gioiHanLanLam;
    }

    public void setGioiHanLanLam(Integer gioiHanLanLam) {
        this.gioiHanLanLam = gioiHanLanLam;
    }

    public List<QuestionRequestDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionRequestDTO> questions) {
        this.questions = questions;
    }
}
