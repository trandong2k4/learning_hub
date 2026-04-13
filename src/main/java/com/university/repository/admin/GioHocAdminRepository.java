package com.university.repository.admin;

import com.university.entity.GioHoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface GioHocAdminRepository extends JpaRepository<GioHoc, UUID> {

}
