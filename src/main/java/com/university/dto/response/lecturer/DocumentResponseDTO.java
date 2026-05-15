package com.university.dto.response.lecturer;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponseDTO {
    private UUID id;
    private String tenTaiLieu;
    private String moTa;
    private String fileTaiLieuUrl;
    private String loaiTaiLieu;
    private LocalDateTime ngayDang;
    private UUID lopHocPhanId;
}
