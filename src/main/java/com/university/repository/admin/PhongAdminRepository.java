package com.university.repository.admin;

import com.university.entity.Phong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface PhongAdminRepository extends JpaRepository<Phong, UUID> {

}
