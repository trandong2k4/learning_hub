package com.university.service.student;

import java.util.List;
import com.university.dto.request.student.ChuongTrinhDaoTaoRequestDTO;
import com.university.repository.student.ChuongTrinhDaoTaoRepository;
import com.university.dto.response.student.ChuongTrinhDaoTaoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor

public class ChuongTrinhDaoTaoService {
    private final ChuongTrinhDaoTaoRepository chuongTrinhDaoTaoRepository;
    public List<ChuongTrinhDaoTaoResponseDTO> getDanhSach(ChuongTrinhDaoTaoRequestDTO request) {
    if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
      return chuongTrinhDaoTaoRepository.findByNganhIdAndKeyword(request.getNganhId(), request.getKeyword());
    }
    return chuongTrinhDaoTaoRepository.findByNganhId(request.getNganhId());
}
}
