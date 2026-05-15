package com.university.dto.request.lecturer;

import com.university.enums.CotDiemEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCotDiemRequestDTO {
    @NotBlank(message = "Tên cột điểm không được để trống")
    private String tenCotDiem;
    
    @NotBlank(message = "Tỷ trọng không được để trống")
    private String tiTrong;
    
    private CotDiemEnum loai;
    
    private Integer thuTuHienThi;
    
    @NotNull(message = "Mã lớp học phần không được để trống")
    private String lopHocPhanId;
}
