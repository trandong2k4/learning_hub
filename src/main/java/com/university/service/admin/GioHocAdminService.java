package com.university.service.admin;

import com.university.dto.request.admin.GioHocAdminRequestDTO;
import com.university.dto.response.admin.ExcelImportResult;
import com.university.dto.response.admin.GioHocAdminResponseDTO;
import com.university.entity.GioHoc;
import com.university.entity.Lich;
import com.university.exception.SimpleMessageException;
import com.university.mapper.admin.GioHocAdminMapper;
import com.university.repository.admin.GioHocAdminRepository;
import com.university.repository.admin.LichAdminRepository;
import com.university.service.admin.excel.GioHocExcelListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GioHocAdminService {

    private final GioHocAdminRepository gioHocAdminRepository;
    private final LichAdminRepository lichAdminRepository;
    private final GioHocAdminMapper gioHocMapper;

    public List<GioHocAdminResponseDTO> getAll() {
        return gioHocAdminRepository.findAllDTO();
    }

    public GioHocAdminResponseDTO getById(UUID id) {
        return gioHocAdminRepository.findDTOById(id);
    }

    public List<GioHocAdminResponseDTO> getByTenGioHoc(String key) {
        return gioHocAdminRepository.searchByTenGioHoc(key);
    }

    @Transactional
    public GioHocAdminResponseDTO create(GioHocAdminRequestDTO dto) {
        if (gioHocAdminRepository.existsByMaGioHoc(dto.getMaGioHoc())) {
            throw new SimpleMessageException("Mã giờ học '" + dto.getMaGioHoc() + "' đã tồn tại");
        }
        if (dto.getThoiGianBatDau() != null && dto.getThoiGianKetThuc() != null &&
                dto.getThoiGianBatDau().isAfter(dto.getThoiGianKetThuc())) {
            throw new SimpleMessageException("Thời gian bắt đầu phải trước thời gian kết thúc");
        }

        GioHoc gioHoc = gioHocMapper.toEntity(dto);
        return gioHocMapper.toResponseDTO(gioHocAdminRepository.save(gioHoc));
    }

    @Transactional
    public GioHocAdminResponseDTO update(UUID id, GioHocAdminRequestDTO dto) {
        GioHoc gioHoc = gioHocAdminRepository.findById(id)
                .orElseThrow(() -> new SimpleMessageException("Giờ học không tồn tại"));

        if (!gioHoc.getMaGioHoc().equals(dto.getMaGioHoc())
                && gioHocAdminRepository.existsByMaGioHoc(dto.getMaGioHoc())) {
            throw new SimpleMessageException("Mã giờ học '" + dto.getMaGioHoc() + "' đã tồn tại");
        }

        if (dto.getThoiGianBatDau() != null && dto.getThoiGianKetThuc() != null &&
                dto.getThoiGianBatDau().isAfter(dto.getThoiGianKetThuc())) {
            throw new SimpleMessageException("Thời gian bắt đầu phải trước thời gian kết thúc");
        }

        gioHocMapper.updateEntity(gioHoc, dto);
        return gioHocMapper.toResponseDTO(gioHocAdminRepository.save(gioHoc));
    }

    @Transactional
    public void delete(UUID id) {
        GioHoc gioHoc = gioHocAdminRepository.findById(id)
                .orElseThrow(() -> new SimpleMessageException("Giờ học không tồn tại"));

        List<Lich> lichs = lichAdminRepository.findAllByGioHocId(id);
        if (!lichs.isEmpty()) {
            throw new SimpleMessageException("Giờ học '" + gioHoc.getTenGioHoc() + "' vẫn còn " + lichs.size()
                    + " lịch học liên kết. Vui lòng xóa lịch học trước.");
        }

        gioHocAdminRepository.delete(gioHoc);
    }

    @Transactional
    public void deleteAllByList(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        try {
            for (UUID uuid : ids) {
                GioHoc gioHoc = gioHocAdminRepository.findById(uuid)
                        .orElseThrow(() -> new SimpleMessageException("Giờ học không tồn tại"));

                if (lichAdminRepository.existsByGioHocId(uuid)) {
                    throw new SimpleMessageException(
                            "Giờ học '" + gioHoc.getTenGioHoc() + "' vẫn còn lịch học liên kết");
                }
            }
            gioHocAdminRepository.deleteAllByIdIn(ids);
        } catch (SimpleMessageException e) {
            throw e;
        } catch (Exception e) {
            throw new SimpleMessageException("Lỗi khi xóa danh sách: " + e.getMessage());
        }
    }

    @Transactional
    public ExcelImportResult importFromExcel(MultipartFile file) throws IOException {
        GioHocExcelListener listener = new GioHocExcelListener(gioHocAdminRepository);
        com.alibaba.excel.EasyExcel.read(file.getInputStream(), GioHocAdminRequestDTO.class, listener).sheet().doRead();
        return listener.getResult();
    }
}
