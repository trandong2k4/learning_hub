package com.university.repository.student;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.university.entity.SubmitExercise;

import java.util.UUID;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface SubmitExerciseStudentsRepository extends JpaRepository<SubmitExercise, UUID> {

    // ================= CHECK CÓ KẾT QUẢ =================
    @Query("""
        SELECT DISTINCT s.exercise.id
        FROM SubmitExercise s
        WHERE s.exercise.id IN :ids
        AND s.diem IS NOT NULL
    """)
    List<UUID> findExerciseIdsHasResult(@Param("ids") List<UUID> ids);

    Optional<SubmitExercise> findTopByExercise_IdAndHocVien_IdOrderByPhienThucHienDesc(
            UUID exerciseId,
            UUID hocVienId);

    int countByExercise_IdAndHocVien_Id(UUID exerciseId, UUID hocVienId);

    List<SubmitExercise> findByExercise_IdAndHocVien_IdOrderByPhienThucHienDescCreatedAtDesc(
            UUID exerciseId,
            UUID hocVienId);

    // ================= CHECK TỒN TẠI =================
    boolean existsByExercise_IdAndHocVien_Id(UUID exerciseId, UUID hocVienId);

    // ================= LẤY DANH SÁCH SUBMIT THEO EXERCISE IDS VÀ HỌC VIÊN ID =================
    @Query("""
        SELECT s FROM SubmitExercise s
        WHERE s.exercise.id IN :exerciseIds
        AND s.hocVien.id = :hocVienId
        ORDER BY s.phienThucHien DESC, s.createdAt DESC
    """)
    List<SubmitExercise> findByExerciseIdsAndHocVienId(
            @Param("exerciseIds") List<UUID> exerciseIds,
            @Param("hocVienId") UUID hocVienId
    );
}
