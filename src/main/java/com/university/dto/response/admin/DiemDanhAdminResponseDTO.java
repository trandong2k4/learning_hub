package com.university.dto.response.admin;

import java.util.UUID;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DiemDanhAdminResponseDTO {

    private UUID id;
    private Boolean trangThai;
    private UUID hocVienId;
    private UUID lichId;

    public interface DiemDanhView {
        UUID getId();
    }

}