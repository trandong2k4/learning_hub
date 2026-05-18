package com.university.dto.response.admin;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.university.enums.GioiTinhEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createAt;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime updateAt;
    private List<String> roles = new ArrayList<>();

    public interface UserView {
        UUID getId();

        String getUserName();

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

    public interface UsersBasicProjection {
        UUID getId();

        String getUserName();

        String getPassWord();

        String getEmail();

        String getCccd();

        String getHoTen();

        String getDiaChi();

        GioiTinhEnum getGioiTinh();

        LocalDateTime getNgaySinh();

        String getSoDienThoai();

        Boolean getTrangThai();

        String getGhiChu();

        LocalDateTime getCreateAt();

        LocalDateTime getUpdateAt();
    }
}