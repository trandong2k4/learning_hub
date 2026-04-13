package com.university.repository.admin;

import com.university.entity.ThongBaoNguoiDung;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ThongBaoNguoiDungAdminRepository extends JpaRepository<ThongBaoNguoiDung, UUID> {

}
