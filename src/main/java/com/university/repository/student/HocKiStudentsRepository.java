package com.university.repository.student;

import com.university.entity.HocKi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HocKiStudentsRepository extends JpaRepository<HocKi, UUID> {

    @Query("SELECT h FROM HocKi h ORDER BY h.ngayBatDau ASC")
    List<HocKi> findAllOrderByNgayBatDau();
}
