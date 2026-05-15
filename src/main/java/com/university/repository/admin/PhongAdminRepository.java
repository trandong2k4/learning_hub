package com.university.repository.admin;

import com.university.entity.Phong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PhongAdminRepository extends JpaRepository<Phong, UUID> {

    boolean existsByMaPhong(String maPhong);

    @Query("SELECT p.maPhong FROM Phong p")
    List<String> findAllMaPhong();

    @Query("SELECT DISTINCT p FROM Phong p LEFT JOIN FETCH p.dLichs")
    List<Phong> findAllWithLichs();

    void deleteAllByIdIn(List<UUID> ids);
}
