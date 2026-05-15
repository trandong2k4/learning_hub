package com.university.dto.response.lecturer;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradeHistoryResponseDTO {
    private UUID historyId;
    private Float diemCu;
    private Float diemMoi;
    private String ghiChu;
    private LocalDateTime thoiGianThayDoi;
    private String nguoiThayDoi;
}
