package com.university.repository.student;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.university.dto.response.student.HocVienProfileResponseDTO;
import com.university.entity.HocVien;

/**
 * Repository truy vấn thông tin hồ sơ của học viên.
 *
 * <p>Kế thừa {@link JpaRepository} để tận dụng các thao tác CRUD cơ bản.
 * Cung cấp thêm các truy vấn JPQL tùy chỉnh để chiếu trực tiếp ra
 * {@link HocVienProfileResponseDTO} (DTO projection), giảm lượng dữ liệu fetch
 * so với load toàn bộ entity.</p>
 */
public interface HocVienProfileRepository extends JpaRepository<HocVien, UUID> {

    /**
     * Lấy hồ sơ học viên theo ID của tài khoản người dùng ({@code Users.id}).
     *
     * <p>Dùng trong luồng học viên tự xem hồ sơ: userId được lấy từ Security Context.</p>
     *
     * @param userId UUID của {@code Users} đang đăng nhập
     * @return Optional chứa DTO hồ sơ, rỗng nếu không tìm thấy
     */
    @Query("""
            SELECT new com.university.dto.response.student.HocVienProfileResponseDTO(
                u.id,
                u.userName,
                u.hoTen,
                u.diaChi,
                u.soDienThoai,
                u.email,
                u.gioiTinh,
                u.ngaySinh,
                u.cccd,
                h.maHocVien,
                n.id,
                h.ngayNhapHoc,
                h.ngayTotNghiep
            )
            FROM HocVien h
            JOIN h.users u
            LEFT JOIN h.nganh n
            WHERE u.id = :userId
            """)
    Optional<HocVienProfileResponseDTO> findHocVienProfileByUserId(UUID userId);

    /**
     * Lấy hồ sơ học viên theo ID của bản ghi học viên ({@code HocVien.id}).
     *
     * <p>Dùng trong luồng admin hoặc giảng viên tra cứu hồ sơ học viên cụ thể.</p>
     *
     * @param hocVienId UUID của entity {@code HocVien}
     * @return Optional chứa DTO hồ sơ, rỗng nếu không tìm thấy
     */
    @Query("""
            SELECT new com.university.dto.response.student.HocVienProfileResponseDTO(
                u.id,
                u.userName,
                u.hoTen,
                u.diaChi,
                u.soDienThoai,
                u.email,
                u.gioiTinh,
                u.ngaySinh,
                u.cccd,
                h.maHocVien,
                n.id,
                h.ngayNhapHoc,
                h.ngayTotNghiep
            )
            FROM HocVien h
            JOIN h.users u
            LEFT JOIN h.nganh n
            WHERE h.id = :hocVienId
            """)
    Optional<HocVienProfileResponseDTO> findHocVienProfileByHocVienId(UUID hocVienId);

    /**
     * Tìm entity {@link HocVien} theo ID người dùng liên kết.
     *
     * @param userId UUID của {@code Users}
     * @return Optional chứa entity HocVien
     */
    Optional<HocVien> findByUsers_Id(UUID userId);
}
