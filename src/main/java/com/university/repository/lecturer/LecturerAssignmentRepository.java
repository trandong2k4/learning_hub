package com.university.repository.lecturer;

import com.university.entity.Exercise;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LecturerAssignmentRepository extends JpaRepository<Exercise, UUID> {
    List<Exercise> findByLopHocPhan_Id(UUID lopHocPhanId);
}
