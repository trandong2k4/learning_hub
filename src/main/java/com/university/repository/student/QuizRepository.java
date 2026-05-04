package com.university.repository.student;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import com.university.entity.Quiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository // 👉 Đánh dấu đây là Spring Data Repository (tầng truy cập DB)
public interface QuizRepository extends JpaRepository<Quiz, UUID> {

    // 📌 1. LẤY DANH SÁCH QUIZ THEO LỚP HỌC PHẦN (CÓ PHÂN TRANG)

    Page<Quiz> findByLopHocPhan_Id(UUID lopHocPhanId, Pageable pageable);

    // 📌 2. LẤY QUIZ ĐANG MỞ
    @Query("""
                SELECT q FROM Quiz q
                WHERE q.lopHocPhan.id = :lopHocPhanId
                AND q.thoiGianBatDau <= :now
                AND q.thoiGianKetThuc >= :now
                ORDER BY q.thoiGianBatDau DESC
            """)
    List<Quiz> findDangMoByLopHocPhanId(
            @Param("lopHocPhanId") UUID lopHocPhanId, // 👉 id lớp học phần
            @Param("now") LocalDateTime now // 👉 thời gian hiện tại
    );
    // 📌 3. LẤY CHI TIẾT QUIZ (FETCH SÂU - TRÁNH N+1 QUERY)

    @EntityGraph(attributePaths = {
            "dQuizExercises",
            "dQuizExercises.exercise",
            "dQuizExercises.exercise.dQuestions",
            "dQuizExercises.exercise.dQuestions.dAnswers"
    })
    // Optional<Quiz> findById(UUID id);
    // 📌 4. TÌM KIẾM QUIZ THEO TIÊU ĐỀ

    @Query("""
                SELECT q FROM Quiz q
                WHERE q.lopHocPhan.id = :lopHocPhanId
                AND LOWER(q.tieuDe) LIKE LOWER(CONCAT('%', :keyword, '%'))
                ORDER BY q.thoiGianBatDau DESC
            """)
    Page<Quiz> searchByTieuDe(
            @Param("lopHocPhanId") UUID lopHocPhanId, // 👉 lọc theo lớp
            @Param("keyword") String keyword, // 👉 từ khóa tìm kiếm
            Pageable pageable // 👉 phân trang + sort
    );
}
