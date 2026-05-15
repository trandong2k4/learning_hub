package com.university.dto.request.admin;

import com.university.enums.LoaiThongBaoEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ThongBaoAdminRequestDTO {

    @NotBlank(message = "Tiêu đề không được để trống")
    private String tieuDe;

    @NotBlank(message = "Nội dung không được để trống")
    private String noiDung;

    private String fileThongBao;

    @NotNull(message = "Loại thông báo không được để trống")
    private LoaiThongBaoEnum loaiThongBao;

    @NotNull(message = "Id người gửi không được để trống")
    private UUID usersId;

    private List<UUID> userIds;
}
