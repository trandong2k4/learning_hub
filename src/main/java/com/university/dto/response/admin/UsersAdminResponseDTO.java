package com.university.dto.response.admin;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.university.enums.GioiTinhEnum;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsersAdminResponseDTO {

    private UUID id;
    private String userName;
    private String passWord;
    private String email;
    private String cccd;
    private String hoTen;
    private String diaChi;
    private GioiTinhEnum gioiTinh;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime ngaySinh;
    private String soDienThoai;
    private Boolean trangThai;
    private String ghiChu;
    @JsonFormat(pattern = "dd/MM/yyyy : hh:mm:ss")
    private LocalDateTime createAt;
    @JsonFormat(pattern = "dd/MM/yyyy : hh:mm:ss")
    private LocalDateTime updateAt;

    public interface UserView {
        UUID getId();

        String getUserName();

        String getPassWord();

        String getEmail();

        String getCccd();

        String getHoTen();

        String getDiaChi();

        String getGioiTinh();

        String getNgaySinh();

        String getSoDienThoai();

        Boolean getTrangThai();

        String getGhiChu();
    }
}