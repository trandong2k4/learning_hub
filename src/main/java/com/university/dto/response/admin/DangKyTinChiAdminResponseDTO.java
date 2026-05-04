package com.university.dto.response.admin;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DangKyTinChiAdminResponseDTO {

    private UUID id;
    private UUID lopHocPhanId;
    private UUID hocVienId;
    private UUID usersId;
    @JsonFormat(pattern = "dd/MM/yyyy : hh:mm:ss")
    private LocalDateTime createdAt;

    public interface DangKyTinChiView {
        UUID getId();

        LopHocPhanInfo getLopHocPhan();

        interface LopHocPhanInfo {
            UUID getId();
        }

        HocVienInfo getHocVien();

        interface HocVienInfo {

            UUID getId();
        }

        UsersInfo getUsers();

        interface UsersInfo {

            UUID getId();
        }

        LocalDateTime getCreatedAt();
    }
}