package com.university.dto.response.student;

import java.time.LocalDateTime;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@AllArgsConstructor

public class DangKyTinChiResponseDTO {

    private UUID id;
    @JsonFormat(pattern = "dd/MM/yyyy : hh:mm:ss")
    private LocalDateTime createdAt;

    private UUID HocVienId;
    private String maHocVien;

    private UUID lopHocPhanId;
    private String maLopHocPhan;

    private UUID monHocId;
    private String maMonHoc;
    private Integer soTinChi;
    

}
