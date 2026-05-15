package com.university.dto.response.student;


import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.university.enums.GioiTinhEnum;

import lombok.*;

/**
 * DTO trả về thông tin hồ sơ đầy đủ của học viên.
 *
 * <p>Được chiếu (projected) trực tiếp từ JPQL trong {@link com.university.repository.student.HocVienProfileRepository},
 * kết hợp dữ liệu từ ba bảng: {@code Users}, {@code HocVien}, và {@code Nganh}.</p>
 *
 * <p>Constructor tường minh cần thiết để JPA có thể khởi tạo DTO từ kết quả truy vấn
 * ({@code new com.university...HocVienProfileResponseDTO(...)} trong JPQL).</p>
 */
@Getter
@Setter
@NoArgsConstructor
public class HocVienProfileResponseDTO {

    /** UUID của tài khoản {@code Users} (dùng để định danh người dùng trong hệ thống). */
    private UUID id;

    /** Tên đăng nhập. */
    private String userName;

    /** Họ và tên đầy đủ. */
    private String hoTen;

    /** Địa chỉ thường trú. */
    private String diaChi;

    /** Số điện thoại liên lạc. */
    private String soDienThoai;

    /** Địa chỉ email. */
    private String email;

    /** Giới tính (MALE / FEMALE / OTHER). */
    private GioiTinhEnum gioiTinh;

    /** Ngày sinh, serialize ra định dạng yyyy-MM-dd (bỏ phần giờ phút giây). */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime ngaySinh;

    /**
     * Số căn cước công dân — lưu nội bộ dạng raw, serialize ra JSON đã được mask.
     * Getter tùy chỉnh bên dưới trả về dạng {@code ********XXXX}.
     */
    @Getter(AccessLevel.NONE)
    private String cccd;

    /** Mã học viên do hệ thống cấp (ví dụ: SV2024001). */
    private String maHocVien;

    /** UUID của ngành học mà học viên đang theo học. */
    private UUID nganhId;

    /** Ngày nhập học chính thức vào trường. */
    private LocalDateTime ngayNhapHoc;

    /** Ngày tốt nghiệp (null nếu học viên chưa tốt nghiệp). */
    private LocalDateTime ngayTotNghiep;

    /** Trả về CCCD đã mask, chỉ hiển thị 4 chữ số cuối (ví dụ: {@code ********2345}). */
    public String getCccd() {
        if (cccd == null || cccd.length() < 4) return "****";
        return "*".repeat(cccd.length() - 4) + cccd.substring(cccd.length() - 4);
    }

    /**
     * Constructor dùng cho JPQL DTO projection.
     *
     * <p>Thứ tự và kiểu dữ liệu tham số phải khớp chính xác với câu truy vấn JPQL
     * trong {@link com.university.repository.student.HocVienProfileRepository}.</p>
     */
    public HocVienProfileResponseDTO(
            UUID id,
            String userName,
            String hoTen,
            String diaChi,
            String soDienThoai,
            String email,
            GioiTinhEnum gioiTinh,
            LocalDateTime ngaySinh,
            String cccd,
            String maHocVien,
            UUID nganhId,
            LocalDateTime ngayNhapHoc,
            LocalDateTime ngayTotNghiep
    ) {
        this.id = id;
        this.userName = userName;
        this.hoTen = hoTen;
        this.diaChi = diaChi;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.gioiTinh = gioiTinh;
        this.ngaySinh = ngaySinh;
        this.cccd = cccd;
        this.maHocVien = maHocVien;
        this.nganhId = nganhId;
        this.ngayNhapHoc = ngayNhapHoc;
        this.ngayTotNghiep = ngayTotNghiep;
    }
}
