package com.university.dto.response.admin;

import java.util.UUID;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MonHocAdminResponseDTO {

    private UUID id;
    private String maMonHoc;
    private String tenMonHoc;
    private Integer soTinChi;
    private String moTa;

    public interface MonHocView {
        UUID getId();
    }

}