package com.university.repository.admin;

import com.university.entity.ThanhToanHocPhi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ThanhToanHocPhiAdminRepository extends JpaRepository<ThanhToanHocPhi, UUID> {

    @Query("SELECT t FROM ThanhToanHocPhi t JOIN FETCH t.hocPhi")
    List<ThanhToanHocPhi> findAllWithHocPhi();

    void deleteAllByIdIn(List<UUID> ids);
}
