package com.university.repository.student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import com.university.entity.QuizAttempt;
import java.util.List;
import java.util.Optional;
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, UUID> {

     // Thêm mới: Kiểm tra đã nộp bài chưa (status = true)
    boolean existsByQuiz_IdAndHocVien_IdAndStatusTrue(UUID quizId, UUID hocVienId);

    // Thêm mới: Lấy attempt đang làm dở (status = false)
    Optional<QuizAttempt> findByQuiz_IdAndHocVien_IdAndStatusFalse(UUID quizId, UUID hocVienId);

    List<QuizAttempt> findByHocVien_IdAndQuiz_IdIn(UUID hocVienId, List<UUID> quizIds);

    Optional<QuizAttempt> findTopByQuiz_IdAndHocVien_IdOrderByStartTimeDesc(UUID quizId, UUID hocVienId);
    
}

