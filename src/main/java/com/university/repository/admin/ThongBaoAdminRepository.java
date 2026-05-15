package com.university.repository.admin;

import com.university.dto.response.admin.ThongBaoAdminResponseDTO;
import com.university.entity.ThongBao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ThongBaoAdminRepository extends JpaRepository<ThongBao, UUID> {

    @Query("""
            SELECT new com.university.dto.response.admin.ThongBaoAdminResponseDTO(
                tb.id,
                tb.tieuDe,
                tb.noiDung,
                tb.fileThongBao,
                tb.loaiThongBao,
                tb.createdAt,
                u.id,
                u.userName,
                u.hoTen,
                COUNT(tbnd.id),
                SUM(CASE WHEN tbnd.daNhan = true THEN 1 ELSE 0 END)
            )
            FROM ThongBao tb
            JOIN tb.users u
            LEFT JOIN tb.nguoiNhanList tbnd
            GROUP BY tb.id, tb.tieuDe, tb.noiDung, tb.fileThongBao, tb.loaiThongBao, tb.createdAt,
                u.id, u.userName, u.hoTen
            ORDER BY tb.createdAt DESC
            """)
    List<ThongBaoAdminResponseDTO> findAllDTO();

    @Query("""
            SELECT new com.university.dto.response.admin.ThongBaoAdminResponseDTO(
                tb.id,
                tb.tieuDe,
                tb.noiDung,
                tb.fileThongBao,
                tb.loaiThongBao,
                tb.createdAt,
                u.id,
                u.userName,
                u.hoTen,
                COUNT(tbnd.id),
                SUM(CASE WHEN tbnd.daNhan = true THEN 1 ELSE 0 END)
            )
            FROM ThongBao tb
            JOIN tb.users u
            LEFT JOIN tb.nguoiNhanList tbnd
            WHERE tb.id = :id
            GROUP BY tb.id, tb.tieuDe, tb.noiDung, tb.fileThongBao, tb.loaiThongBao, tb.createdAt,
                u.id, u.userName, u.hoTen
            """)
    ThongBaoAdminResponseDTO findDTOById(@Param("id") UUID id);

    @Query("""
            SELECT new com.university.dto.response.admin.ThongBaoAdminResponseDTO(
                tb.id,
                tb.tieuDe,
                tb.noiDung,
                tb.fileThongBao,
                tb.loaiThongBao,
                tb.createdAt,
                u.id,
                u.userName,
                u.hoTen,
                COUNT(tbnd.id),
                SUM(CASE WHEN tbnd.daNhan = true THEN 1 ELSE 0 END)
            )
            FROM ThongBao tb
            JOIN tb.users u
            LEFT JOIN tb.nguoiNhanList tbnd
            WHERE u.id = :usersId
            GROUP BY tb.id, tb.tieuDe, tb.noiDung, tb.fileThongBao, tb.loaiThongBao, tb.createdAt,
                u.id, u.userName, u.hoTen
            ORDER BY tb.createdAt DESC
            """)
    List<ThongBaoAdminResponseDTO> findAllByUsersIdDTO(@Param("usersId") UUID usersId);

    void deleteAllByIdIn(List<UUID> ids);
}
