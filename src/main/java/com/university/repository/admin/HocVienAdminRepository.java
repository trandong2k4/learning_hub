package com.university.repository.admin;

import com.university.dto.response.admin.HocVienAdminResponseDTO;
import com.university.entity.HocVien;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HocVienAdminRepository extends JpaRepository<HocVien, UUID> {

    List<HocVienAdminResponseDTO.HocVienView> findAllProjectedBy();

    Optional<HocVien> findByMaHocVien(String maHocVien);

    Optional<HocVien> findByUsersId(UUID usersId);

    @Query("SELECT COUNT(hv) FROM HocVien hv WHERE hv.ngayTotNghiep IS NULL")
    long countByNgayTotNghiepIsNull();

    @Query("SELECT COUNT(hv) FROM HocVien hv WHERE hv.ngayTotNghiep IS NOT NULL")
    long countByNgayTotNghiepIsNotNull();

    @Query("SELECT n.tenNganh, COUNT(s.id) FROM HocVien s JOIN s.nganh n WHERE s.nganh IS NOT NULL GROUP BY n.tenNganh")
    List<Object[]> countByNganhRaw();

    @Query("SELECT EXTRACT(YEAR FROM s.ngayNhapHoc), COUNT(s.id) FROM HocVien s WHERE s.ngayNhapHoc IS NOT NULL GROUP BY EXTRACT(YEAR FROM s.ngayNhapHoc)")
    List<Object[]> countByNamNhapHocRaw();

    void deleteAllByIdIn(List<UUID> ids);
}
