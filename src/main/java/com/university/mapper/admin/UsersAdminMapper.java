package com.university.mapper.admin;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import com.university.dto.request.admin.UsersAdminRequestDTO;
import com.university.dto.response.admin.UsersAdminResponseDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.university.entity.Users;

@Component
public class UsersAdminMapper {

    private final PasswordEncoder passwordEncoder;

    public UsersAdminMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public Users toEntity(UsersAdminRequestDTO dto) {
        Users user = new Users();
        user.setUserName(dto.getUserName());
        user.setPassWord(passwordEncoder.encode(dto.getPassWord()));
        user.setEmail(dto.getEmail());
        user.setCccd(dto.getCccd());
        user.setHoTen(dto.getHoTen());
        user.setDiaChi(dto.getDiaChi());
        user.setGioiTinh(dto.getGioiTinh());
        user.setNgaySinh(dto.getNgaySinh().atStartOfDay());
        user.setSoDienThoai(dto.getSoDienThoai());
        user.setTrangThai(dto.getTrangThai());
        user.setGhiChu(dto.getGhiChu());
        user.setCreateAt(LocalDateTime.now());
        user.setUpdateAt(LocalDateTime.now());
        return user;
    }

    public UsersAdminResponseDTO updateEntity(Users users, UsersAdminRequestDTO dto) {
        users.setUserName(dto.getUserName());
        users.setPassWord(passwordEncoder.encode(dto.getPassWord()));
        users.setEmail(dto.getEmail());
        users.setCccd(dto.getCccd());
        users.setHoTen(dto.getHoTen());
        users.setDiaChi(dto.getDiaChi());
        users.setGioiTinh(dto.getGioiTinh());
        users.setNgaySinh(dto.getNgaySinh().atStartOfDay());
        users.setSoDienThoai(dto.getSoDienThoai());
        users.setTrangThai(dto.getTrangThai());
        users.setGhiChu(dto.getGhiChu());
        users.setUpdateAt(LocalDateTime.now());

        return toResponseDTO(users);
    }

    public UsersAdminResponseDTO toResponseDTO(Users users) {
        UsersAdminResponseDTO dto = new UsersAdminResponseDTO();
        dto.setId(users.getId());
        dto.setUserName(users.getUsername());
        dto.setPassWord(users.getPassword());
        dto.setEmail(users.getEmail());
        dto.setCccd(users.getCccd());
        dto.setHoTen(users.getHoTen());
        dto.setDiaChi(users.getDiaChi());
        dto.setGioiTinh(users.getGioiTinh());
        dto.setNgaySinh(users.getNgaySinh());
        dto.setSoDienThoai(users.getSoDienThoai());
        dto.setTrangThai(users.isTrangThai());
        dto.setGhiChu(users.getGhiChu());
        dto.setCreateAt(users.getCreateAt());
        dto.setUpdateAt(users.getUpdateAt());
        return dto;
    }
}
