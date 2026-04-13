package com.university.repository.admin;

import com.university.entity.MonHocTienQuyet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface MocHocTienQuyetAdminRepository extends JpaRepository<MonHocTienQuyet, UUID> {

}
