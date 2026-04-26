package com.university.dto.response.admin;

import java.util.UUID;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChuongTrinhDaoTaoAdminResponseDTO {

    private UUID id;
    private UUID nganhId;
    private UUID monHocId;

    public interface ChuongTrinhDaoTaoView {
        UUID getId();

        UUID getNganhId();

        UUID getMonHocId();

    }

}