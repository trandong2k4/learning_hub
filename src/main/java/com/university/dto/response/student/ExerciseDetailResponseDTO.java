package com.university.dto.response.student;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.university.enums.ExerciseEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseDetailResponseDTO {
    private UUID id;
    private String tieuDe;
    private String moTa;
    private String fileExerciseUrl;
    private LocalDateTime thoiGianBatDau;
    private LocalDateTime thoiGianKetThuc;
    private Integer gioiHanLanLam;
    private String maLopHocPhan;
    private String tenMonHoc;
    private ExerciseEnum trangThai;
    private boolean daCoKetQua;
    private Integer soLanDaLam;
    private Integer phienHienTai;
    private Double diem;
    private String ghiChu;
    private List<ExerciseQuestionDTO> questions;
    private List<ExerciseSubmissionHistoryDTO> submissionHistory;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ExerciseQuestionDTO {
        private UUID questionId;
        private String noiDung;
        private Boolean loaiCauHoi;
        private Boolean nhieuDapAn;
        private Float diem;
        private List<ExerciseAnswerDTO> answers;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class ExerciseAnswerDTO {
            private UUID answerId;
            private String keyAnswers;
            private String conText;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ExerciseSubmissionHistoryDTO {
        private UUID submissionId;
        private Integer phienThucHien;
        private String fileExerciseUrl;
        private LocalDateTime thoiGianNop;
        private Double diem;
        private String ghiChu;
        private List<ExerciseSubmissionAnswerDTO> answers;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ExerciseSubmissionAnswerDTO {
        private UUID questionId;
        private UUID answerId;
        private String keyAnswers;
        private String answerText;
        private String noiDungTuLuan;
        private Boolean isCorrect;
        private Float diemDatDuoc;
    }
}
