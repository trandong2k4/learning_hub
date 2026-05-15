package com.university.dto.request.Notification;

import java.util.List;
import java.util.UUID;

import com.university.enums.LoaiThongBaoEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationRequest {

    @NotBlank(message = "Tiêu đề không được để trống")
    private String tieuDe;

    @NotBlank(message = "Nội dung không được để trống")
    private String noiDung;

    @NotNull(message = "Loại thông báo không được để trống")
    private LoaiThongBaoEnum loaiThongBao;

    private String fileThongBao;

    @NotEmpty(message = "Danh sách người nhận không được rỗng")
    private List<@NotNull(message = "UserId không được null") UUID> userIds;
}
