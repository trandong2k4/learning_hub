package com.university.repository.admin;

import com.university.dto.response.admin.HocPhiAdminResponseDTO;
import com.university.entity.HocPhi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HocPhiAdminRepository extends JpaRepository<HocPhi, UUID> {
    List<HocPhiAdminResponseDTO.HocPhiView> findAllProjectedBy();

    void deleteAllByIdIn(List<UUID> ids);
}
