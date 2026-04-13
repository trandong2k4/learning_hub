package com.university.repository.student;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.university.entity.TaiLieu;
import com.university.dto.response.student.TaiLieuStudentsResponseDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;



public interface TaiLieuStudentsRepository extends JpaRepository<TaiLieu, UUID> {
   @Query("""
        SELECT new com.university.dto.response.student.TaiLieuStudentsResponseDTO(
            t.id,
            t.tenTaiLieu,
            t.moTa,
            t.fileTaiLieuUrl,
            t.loaiTaiLieu,
            t.ngayDang,
            t.lopHocPhan.id
        )
        FROM TaiLieu t  
        WHERE t.lopHocPhan.id = :lophocphanId
        ORDER BY t.ngayDang DESC
    """)
    List<TaiLieuStudentsResponseDTO> findByLopHocPhanId(@Param("lophocphanId") UUID lophocphanId);    

    @Query("""
        SELECT new com.university.dto.response.student.TaiLieuStudentsResponseDTO(
            t.id,
            t.tenTaiLieu,
            t.moTa,
            t.fileTaiLieuUrl,
            t.loaiTaiLieu,
            t.ngayDang,
            t.lopHocPhan.id
        )
        FROM TaiLieu t
        WHERE(:lophocphanId IS NULL OR t.lopHocPhan.id = :lophocphanId)
        AND (:keyword IS NULL OR LOWER(t.tenTaiLieu) LIKE LOWER(CONCAT('%', :keyword, '%')))
        AND (:loaiTaiLieu IS NULL OR t.loaiTaiLieu = :loaiTaiLieu)
    
    """)
    List<TaiLieuStudentsResponseDTO> searchTaiLieu(
        @Param("lophocphanId") UUID lophocphanId,
        @Param("keyword") String keyword,
        @Param("loaiTaiLieu") String loaiTaiLieu
    );
}
                

