package com.university.repository.admin;

import com.university.entity.LichSuLienHe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LichSuLienHeAdminRepository extends JpaRepository<LichSuLienHe, UUID> {
    void deleteAllByIdIn(List<UUID> ids);
}
