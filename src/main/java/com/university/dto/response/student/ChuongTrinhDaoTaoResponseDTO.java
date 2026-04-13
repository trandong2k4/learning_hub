package com.university.dto.response.student;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;   

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChuongTrinhDaoTaoResponseDTO {
    private UUID id;
    private String maNganh;
    private String tenNganh;
    private String maMonHoc;
    private String tenMonHoc;
    private Integer soTinChi;
    private String moTa;
    
}
