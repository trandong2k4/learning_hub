package com.university.repository.admin;

import com.university.entity.GiangDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface GiangDayAdminRepository extends JpaRepository<GiangDay, UUID> {

}
