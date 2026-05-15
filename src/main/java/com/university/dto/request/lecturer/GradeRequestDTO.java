package com.university.dto.request.lecturer;

import java.util.Map;
import jakarta.validation.constraints.NotNull;

public class GradeRequestDTO {
    @NotNull(message = "Mã lớp học phần không được để trống")
    private String lopHocPhanId;

    @NotNull(message = "Danh sách điểm không được để trống")
    private Map<String, Float> studentGrades;

    public String getLopHocPhanId() {
        return lopHocPhanId;
    }

    public void setLopHocPhanId(String lopHocPhanId) {
        this.lopHocPhanId = lopHocPhanId;
    }

    public Map<String, Float> getStudentGrades() {
        return studentGrades;
    }

    public void setStudentGrades(Map<String, Float> studentGrades) {
        this.studentGrades = studentGrades;
    }
}
