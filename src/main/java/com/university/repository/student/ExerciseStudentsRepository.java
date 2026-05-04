package com.university.repository.student;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import com.university.entity.Exercise;
import java.time.LocalDateTime;
import org.springframework.data.repository.query.Param;

@Repository
public interface ExerciseStudentsRepository extends JpaRepository<Exercise, UUID> {

    //  Method này có thể không cần nếu đã dùng phân trang bên dưới
    //  Nên xóa để tránh dư thừa
    List<Exercise> findByLopHocPhan_Id(UUID lopHocPhanId);

    //  Lấy danh sách bài tập có phân trang
    Page<Exercise> findByLopHocPhan_Id(UUID lopHocPhanId, Pageable pageable);

    //  Lấy bài tập đang mở
    //  GỢI Ý: Nên dùng @Param để tránh lỗi "Parameter not bound"
    @Query("""
        SELECT e FROM Exercise e
        WHERE e.lopHocPhan.id = :lopHocPhanId
        AND e.thoiGianBatDau <= :now
        AND e.thoiGianKetThuc >= :now
    """)
    List<Exercise> findDangMoByLopHocPhanId(
            @Param("lopHocPhanId") UUID lopHocPhanId,
            @Param("now") LocalDateTime now
    );

    //  Bài tập sắp mở
    // Có ORDER BY giúp dữ liệu hiển thị đúng thứ tự
    @Query("""
        SELECT e FROM Exercise e
        WHERE e.lopHocPhan.id = :lopHocPhanId
        AND e.thoiGianBatDau > :now
        ORDER BY e.thoiGianBatDau ASC
    """)
    List<Exercise> findSapMoByLopHocPhanId(
            @Param("lopHocPhanId") UUID lopHocPhanId,
            @Param("now") LocalDateTime now
    );

    //  Bài tập đã đóng
    @Query("""
        SELECT e FROM Exercise e
        WHERE e.lopHocPhan.id = :lopHocPhanId
        AND e.thoiGianKetThuc < :now
        ORDER BY e.thoiGianKetThuc DESC
    """)
    List<Exercise> findDaDongByLopHocPhanId(
            @Param("lopHocPhanId") UUID lopHocPhanId,
            @Param("now") LocalDateTime now
    );

    //  Tìm kiếm theo tiêu đề:
    //   ORDER BY để tránh kết quả bị random
    // - Có thể mở rộng search theo mô tả (moTa) nếu cần
    @Query("""
        SELECT e FROM Exercise e
        WHERE e.lopHocPhan.id = :lopHocPhanId
        AND LOWER(e.tieuDe) LIKE LOWER(CONCAT('%', :keyword, '%'))
        ORDER BY e.thoiGianBatDau DESC
    """)
    Page<Exercise> searchByTieuDe(
            @Param("lopHocPhanId") UUID lopHocPhanId,
            @Param("keyword") String keyword,
            Pageable pageable
    );
    // - Method này tên rất dài → khó maintain
    // - Có thể thay bằng @Query cho dễ đọc hơn
    // - Dùng để check có bài tập nào đang mở hay không
    boolean existsByLopHocPhan_IdAndThoiGianBatDauLessThanEqualAndThoiGianKetThucGreaterThanEqual(
            UUID lopHocPhanId,
            LocalDateTime now1,
            LocalDateTime now2
    );
 
}