package com.university.repository.admin;

import com.university.dto.response.admin.GiangDayAdminResponseDTO.GiangDayView;
import com.university.entity.GiangDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GiangDayAdminRepository extends JpaRepository<GiangDay, UUID> {
    List<GiangDayView> findAllProjectedBy();

    void deleteAllByIdIn(List<UUID> ids);
}
