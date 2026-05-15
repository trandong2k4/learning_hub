package com.university.repository.admin;

import com.university.dto.response.admin.ThongBaoNguoiDungAdminResponseDTO;
import com.university.entity.ThongBaoNguoiDung;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ThongBaoNguoiDungAdminRepository extends JpaRepository<ThongBaoNguoiDung, UUID> {

    List<ThongBaoNguoiDung> findByUsers_Id(UUID userId);

    List<ThongBaoNguoiDung> findByUsers_IdAndDaNhanFalse(UUID userId);

    Optional<ThongBaoNguoiDung> findByThongBao_IdAndUsers_Id(UUID thongBaoId, UUID userId);

    boolean existsByThongBao_IdAndUsers_Id(UUID thongBaoId, UUID userId);

    boolean existsByThongBao_IdAndUsers_IdAndIdNot(UUID thongBaoId, UUID userId, UUID id);

    @Query("""
            SELECT new com.university.dto.response.admin.ThongBaoNguoiDungAdminResponseDTO(
                tbnd.id,
                tbnd.daNhan,
                u.id,
                u.userName,
                u.hoTen,
                tb.id,
                tb.tieuDe
            )
            FROM ThongBaoNguoiDung tbnd
            JOIN tbnd.users u
            JOIN tbnd.thongBao tb
            ORDER BY tb.createdAt DESC
            """)
    List<ThongBaoNguoiDungAdminResponseDTO> findAllDTO();

    @Query("""
            SELECT new com.university.dto.response.admin.ThongBaoNguoiDungAdminResponseDTO(
                tbnd.id,
                tbnd.daNhan,
                u.id,
                u.userName,
                u.hoTen,
                tb.id,
                tb.tieuDe
            )
            FROM ThongBaoNguoiDung tbnd
            JOIN tbnd.users u
            JOIN tbnd.thongBao tb
            WHERE tbnd.id = :id
            """)
    ThongBaoNguoiDungAdminResponseDTO findDTOById(@Param("id") UUID id);

    @Query("""
            SELECT new com.university.dto.response.admin.ThongBaoNguoiDungAdminResponseDTO(
                tbnd.id,
                tbnd.daNhan,
                u.id,
                u.userName,
                u.hoTen,
                tb.id,
                tb.tieuDe
            )
            FROM ThongBaoNguoiDung tbnd
            JOIN tbnd.users u
            JOIN tbnd.thongBao tb
            WHERE tb.id = :thongBaoId
            ORDER BY u.hoTen ASC
            """)
    List<ThongBaoNguoiDungAdminResponseDTO> findAllByThongBaoIdDTO(@Param("thongBaoId") UUID thongBaoId);

    @Query("""
            SELECT new com.university.dto.response.admin.ThongBaoNguoiDungAdminResponseDTO(
                tbnd.id,
                tbnd.daNhan,
                u.id,
                u.userName,
                u.hoTen,
                tb.id,
                tb.tieuDe
            )
            FROM ThongBaoNguoiDung tbnd
            JOIN tbnd.users u
            JOIN tbnd.thongBao tb
            WHERE u.id = :userId
            ORDER BY tb.createdAt DESC
            """)
    List<ThongBaoNguoiDungAdminResponseDTO> findAllByUserIdDTO(@Param("userId") UUID userId);

    void deleteAllByIdIn(List<UUID> ids);
}
