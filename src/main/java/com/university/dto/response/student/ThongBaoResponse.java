package com.university.dto.response.student;

import java.time.LocalDateTime;
import java.util.UUID;

import com.university.enums.LoaiThongBaoEnum;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ThongBaoResponse {
    private UUID id;
    private String tieuDe;
    private String noiDung;
    private String fileThongBao;
    private LoaiThongBaoEnum loaiThongBao;
    private LocalDateTime createdAt;

    private UUID thongbaonguoidungId;
    private Boolean daNhan;
}
