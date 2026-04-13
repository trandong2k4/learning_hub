package com.university.repository.student;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
    

import com.university.entity.HocVien;
import com.university.dto.response.student.HocVienProfileResponseDTO;



public interface HocVienProfileRepository extends JpaRepository<HocVien, UUID> {
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
JOIN h.nganh n
WHERE u.id = :userId
""")
Optional<HocVienProfileResponseDTO> findHocVienProfileByUserId(UUID userId);
    // Optional<HocVienProfileResponseDTO> findHocVienProfileByUserId(@Param("userId") UUID userId);

Optional<HocVien> findByUsers_Id(UUID userId);
}
