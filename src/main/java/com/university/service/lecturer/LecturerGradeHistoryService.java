package com.university.service.lecturer;

import com.university.dto.response.lecturer.GradeHistoryResponseDTO;
import com.university.entity.LichSuDiem;
import com.university.entity.Users;
import com.university.repository.admin.UsersAdminRepository;
import com.university.repository.lecturer.LecturerLichSuDiemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LecturerGradeHistoryService {

    private final LecturerLichSuDiemRepository lichSuDiemRepository;
    private final LecturerValidationService validationService;
    private final UsersAdminRepository userRepository;

    public void recordGradeChange(UUID diemThanhPhanId, Float diemCu, Float diemMoi, UUID nguoiThayDoiId, String ghiChu) {
        Users nguoiThayDoi = userRepository.findById(nguoiThayDoiId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người thay đổi."));

        LichSuDiem history = new LichSuDiem();
        history.setDiemCu(diemCu);
        history.setDiemMoi(diemMoi);
        history.setGhiChu(ghiChu);
        history.setThoiGianThayDoi(LocalDateTime.now());
        history.setNguoiThayDoi(nguoiThayDoi);
        lichSuDiemRepository.save(history);
    }

    @Transactional(readOnly = true)
    public List<GradeHistoryResponseDTO> getGradeHistory(UUID lopHocPhanId, UUID hocVienId, UUID userId) {
        validationService.validateLecturerAssignment(userId, lopHocPhanId);
        return lichSuDiemRepository.findByStudentAndClass(userId, hocVienId, lopHocPhanId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private GradeHistoryResponseDTO toDTO(LichSuDiem ls) {
        return new GradeHistoryResponseDTO(
                ls.getId(),
                ls.getDiemCu(),
                ls.getDiemMoi(),
                ls.getGhiChu(),
                ls.getThoiGianThayDoi(),
                ls.getNguoiThayDoi().getHoTen()
        );
    }
}
