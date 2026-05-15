package com.university.dto.response.admin;

import java.util.UUID;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ThongBaoNguoiDungAdminResponseDTO {

    private UUID id;
    private Boolean daNhan;
    private UUID userId;
    private String userName;
    private String hoTen;
    private UUID thongBaoId;
    private String tieuDe;

    public interface ThongBaoNguoiDungView {
        UUID getId();

        Boolean getDaNhan();

        UUID getUserId();
    }

}
