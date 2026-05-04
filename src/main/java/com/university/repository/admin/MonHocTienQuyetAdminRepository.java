package com.university.repository.admin;

import com.university.entity.MonHocTienQuyet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MonHocTienQuyetAdminRepository extends JpaRepository<MonHocTienQuyet, UUID> {
    void deleteAllByIdIn(List<UUID> ids);
}
