package com.university.repository.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.university.entity.ThongBao;

import java.util.UUID;

@Repository
public interface ThongBaoAdminRepository extends JpaRepository<ThongBao, UUID> {

}
