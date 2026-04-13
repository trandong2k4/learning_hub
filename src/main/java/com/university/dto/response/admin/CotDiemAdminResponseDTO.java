package com.university.dto.response.admin;

import java.util.UUID;
import com.university.enums.CotDiemEnum;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CotDiemAdminResponseDTO {

    private UUID id;
    private String tenCotDiem;
    private String tiTrong;
    private CotDiemEnum loai;
    private Integer thuTuHienThi;

    public interface CotDiemView {
        UUID getId();

    }

}