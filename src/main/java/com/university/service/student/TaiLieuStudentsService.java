package com.university.service.student;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import com.university.dto.response.student.TaiLieuStudentsResponseDTO;
import com.university.repository.student.TaiLieuStudentsRepository;
import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor

public class TaiLieuStudentsService {
    private final TaiLieuStudentsRepository taiLieuStudentsRepository;

    public List<TaiLieuStudentsResponseDTO> getDanhSachTaiLieu(UUID lopHocPhanId) {
        return taiLieuStudentsRepository.findByLopHocPhanId(lopHocPhanId);
    }
    public List<TaiLieuStudentsResponseDTO> searchTaiLieu(UUID lopHocPhanId, String keyword, String loaiTaiLieu) {
        return taiLieuStudentsRepository.searchTaiLieu(lopHocPhanId, keyword, loaiTaiLieu);
    }
}
