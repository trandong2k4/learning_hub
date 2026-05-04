package com.university.repository.admin;

import com.university.dto.response.admin.HocKiAdminResponseDTO;
import com.university.entity.HocKi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HocKiAdminRepository extends JpaRepository<HocKi, UUID> {

    List<HocKiAdminResponseDTO.HocKiView> findAllProjectedBy();

    boolean existsByMaHocKi(String maHocKi);

    void deleteAllByIdIn(List<UUID> ids);

}