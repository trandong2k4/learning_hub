package com.university.dto.response.lecturer;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradeColumnDTO {
    private UUID cotDiemId;
    private String tenCotDiem;
    private String tiTrong;
    private String loai;
    private Integer thuTuHienThi;
}
