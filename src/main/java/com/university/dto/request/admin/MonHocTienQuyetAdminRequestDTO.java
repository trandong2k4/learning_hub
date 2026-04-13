package com.university.dto.request.admin;

import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonHocTienQuyetAdminRequestDTO {

    // Ma mon ton tai trong bang mon hoc
    @NotBlank(message = "Mã môn học không được để trống, tồn tại trong bảng môn học")
    private String maMonHoc;
    @NotNull(message = "Id môn học không được để trống")
    private UUID monHocId;
}