package com.university.repository.admin;

import com.university.dto.response.admin.BaiVietAdminResponseDTO;
import com.university.entity.BaiViet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BaiVietAdminRepository extends JpaRepository<BaiViet, UUID> {
    @Query("SELECT b FROM BaiViet b WHERE LOWER(b.tieuDe) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<BaiViet> searchByTieuDe(@Param("keyword") String keyword);

    @Query("""
                SELECT
                    bv.id AS id,
                    bv.tieuDe AS tieuDe,
                    bv.noiDung AS noiDung,
                    bv.ngayDang AS ngayDang,
                    bv.tacGia AS tacGia,
                    bv.fileDinhKemUrl AS fileDinhKemUrl,
                    bv.hinhAnhUrl AS hinhAnhUrl,
                    bv.loaiBaiViet AS loaiBaiViet,
                    bv.trangThai AS trangThai,
                    bv.createdAt AS createdAt,
                    bv.updatedAt AS updatedAt
                FROM BaiViet bv
            """)
    List<BaiVietAdminResponseDTO.BaiVietView> findAllBaiVietView();
}
