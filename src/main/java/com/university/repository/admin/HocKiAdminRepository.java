package com.university.repository.admin;

import com.university.entity.HocKi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HocKiAdminRepository extends JpaRepository<HocKi, UUID> {

    boolean existsByMaHocKi(String maHocKi);

}