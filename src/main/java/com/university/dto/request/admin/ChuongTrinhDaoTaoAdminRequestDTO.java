package com.university.dto.request.admin;

import java.util.UUID;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChuongTrinhDaoTaoAdminRequestDTO {

    @NotNull(message = "Id ngành không được để trống")
    private UUID nganhId;
    @NotNull(message = "Id môn học không được để trống")
    private UUID monHocId;
}