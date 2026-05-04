package com.university.repository.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.university.entity.ThongBao;

import java.util.List;
import java.util.UUID;

@Repository
public interface ThongBaoAdminRepository extends JpaRepository<ThongBao, UUID> {
    void deleteAllByIdIn(List<UUID> ids);
}
