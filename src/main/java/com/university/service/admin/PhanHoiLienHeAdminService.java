package com.university.service.admin;

import com.university.dto.request.admin.PhanHoiLienHeAdminRequestDTO;
import com.university.dto.request.admin.PhanHoiLienHeReplyRequestDTO;
import com.university.dto.request.admin.PhanHoiLienHeStatusRequestDTO;
import com.university.dto.response.admin.LichSuXuLyLienHeAdminResponseDTO;
import com.university.dto.response.admin.PhanHoiLienHeAdminResponseDTO;
import com.university.entity.LichSuXuLyLienHe;
import com.university.entity.PhanHoiLienHe;
import com.university.enums.TrangThaiXuLyLienHeEnum;
import com.university.exception.SimpleMessageException;
import com.university.mapper.admin.PhanHoiLienHeAdminMapper;
import com.university.repository.admin.LichSuXuLyLienHeAdminRepository;
import com.university.repository.admin.PhanHoiLienHeAdminRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PhanHoiLienHeAdminService {

    private final PhanHoiLienHeAdminRepository phanHoiRepository;
    private final LichSuXuLyLienHeAdminRepository lichSuRepository;
    private final PhanHoiLienHeAdminMapper phanHoiMapper;

    public PhanHoiLienHeAdminResponseDTO getById(UUID id) {
        PhanHoiLienHe phanHoi = phanHoiRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Phản hồi liên hệ không tồn tại"));

        List<LichSuXuLyLienHeAdminResponseDTO> lichSuList = lichSuRepository
                .findAllByPhanHoiIdDTO(id)
                .stream()
                .collect(Collectors.toList());

        return phanHoiMapper.toResponseDTO(phanHoi, lichSuList);
    }

    public List<PhanHoiLienHeAdminResponseDTO> getAll() {
        return phanHoiRepository.findAllDTO();
    }

    public List<PhanHoiLienHeAdminResponseDTO> getAllByTrangThai(TrangThaiXuLyLienHeEnum trangThai) {
        return phanHoiRepository.findAllByTrangThaiDTO(trangThai);
    }

    public List<PhanHoiLienHeAdminResponseDTO> search(String keyword, TrangThaiXuLyLienHeEnum trangThai) {
        String cleaned = keyword == null ? "" : keyword.trim();

        if (trangThai != null && !cleaned.isBlank()) {
            return phanHoiRepository.searchByKeywordAndTrangThaiDTO(cleaned, trangThai);
        } else if (trangThai != null) {
            return phanHoiRepository.findAllByTrangThaiDTO(trangThai);
        } else if (!cleaned.isBlank()) {
            return phanHoiRepository.searchByKeywordDTO(cleaned);
        }
        return phanHoiRepository.findAllDTO();
    }

    public List<PhanHoiLienHeAdminResponseDTO> getByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw new SimpleMessageException("Ngày bắt đầu và ngày kết thúc không được để trống");
        }
        LocalDateTime start = startDate.toLocalDate().atStartOfDay();
        LocalDateTime end = endDate.toLocalDate().atTime(LocalTime.MAX);
        return phanHoiRepository.findByDateRangeDTO(start, end);
    }

    @Transactional
    public PhanHoiLienHeAdminResponseDTO create(PhanHoiLienHeAdminRequestDTO request) {
        normalizeRequest(request);

        PhanHoiLienHe phanHoi = phanHoiMapper.toEntity(request);
        PhanHoiLienHe saved = phanHoiRepository.save(phanHoi);

        LichSuXuLyLienHe lichSu = new LichSuXuLyLienHe();
        lichSu.setTrangThaiTruoc(null);
        lichSu.setTrangThaiMoi(saved.getTrangThai());
        lichSu.setNguoiThucHien("Hệ thống");
        lichSu.setGhiChu("Tạo mới phản hồi liên hệ");
        saved.addLichSuXuLy(lichSu);

        PhanHoiLienHe updated = phanHoiRepository.save(saved);

        List<LichSuXuLyLienHeAdminResponseDTO> lichSuList = lichSuRepository
                .findAllByPhanHoiIdDTO(updated.getId())
                .stream()
                .collect(Collectors.toList());

        return phanHoiMapper.toResponseDTO(updated, lichSuList);
    }

    @Transactional
    public PhanHoiLienHeAdminResponseDTO update(UUID id, PhanHoiLienHeAdminRequestDTO request) {
        normalizeRequest(request);

        PhanHoiLienHe existing = phanHoiRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Phản hồi liên hệ không tồn tại"));

        phanHoiMapper.updateEntity(existing, request);
        PhanHoiLienHe saved = phanHoiRepository.save(existing);

        List<LichSuXuLyLienHeAdminResponseDTO> lichSuList = lichSuRepository
                .findAllByPhanHoiIdDTO(saved.getId())
                .stream()
                .collect(Collectors.toList());

        return phanHoiMapper.toResponseDTO(saved, lichSuList);
    }

    @Transactional
    public PhanHoiLienHeAdminResponseDTO updateStatus(UUID id, PhanHoiLienHeStatusRequestDTO request) {
        if (request == null) {
            throw new SimpleMessageException("Thông tin cập nhật không được để trống");
        }

        PhanHoiLienHe phanHoi = phanHoiRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Phản hồi liên hệ không tồn tại"));

        TrangThaiXuLyLienHeEnum trangThaiCu = phanHoi.getTrangThai();
        TrangThaiXuLyLienHeEnum trangThaiMoi = request.getTrangThai();

        phanHoi.setTrangThai(trangThaiMoi);
        if (request.getNguoiXuLy() != null && !request.getNguoiXuLy().isBlank()) {
            phanHoi.setNguoiXuLy(request.getNguoiXuLy());
        }

        LichSuXuLyLienHe lichSu = new LichSuXuLyLienHe();
        lichSu.setTrangThaiTruoc(trangThaiCu);
        lichSu.setTrangThaiMoi(trangThaiMoi);
        lichSu.setNguoiThucHien(request.getNguoiXuLy() != null ? request.getNguoiXuLy() : "Admin");
        lichSu.setGhiChu(request.getGhiChu());
        phanHoi.addLichSuXuLy(lichSu);

        PhanHoiLienHe saved = phanHoiRepository.save(phanHoi);

        List<LichSuXuLyLienHeAdminResponseDTO> lichSuList = lichSuRepository
                .findAllByPhanHoiIdDTO(saved.getId())
                .stream()
                .collect(Collectors.toList());

        return phanHoiMapper.toResponseDTO(saved, lichSuList);
    }

    @Transactional
    public PhanHoiLienHeAdminResponseDTO reply(UUID id, PhanHoiLienHeReplyRequestDTO request, String nguoiTraLoi) {
        if (request == null || request.getNoiDungPhanHoi() == null) {
            throw new SimpleMessageException("Nội dung phản hồi không được để trống");
        }

        PhanHoiLienHe phanHoi = phanHoiRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Phản hồi liên hệ không tồn tại"));

        TrangThaiXuLyLienHeEnum trangThaiCu = phanHoi.getTrangThai();
        if (trangThaiCu == TrangThaiXuLyLienHeEnum.CHUA_XU_LY) {
            phanHoi.setTrangThai(TrangThaiXuLyLienHeEnum.DANG_XU_LY);
        }

        LichSuXuLyLienHe lichSu = new LichSuXuLyLienHe();
        lichSu.setTrangThaiTruoc(trangThaiCu);
        lichSu.setTrangThaiMoi(phanHoi.getTrangThai());
        lichSu.setNguoiThucHien(nguoiTraLoi != null ? nguoiTraLoi : "Admin");
        lichSu.setNoiDungPhanHoi(request.getNoiDungPhanHoi());
        lichSu.setGhiChu("Phản hồi liên hệ");
        phanHoi.addLichSuXuLy(lichSu);

        PhanHoiLienHe saved = phanHoiRepository.save(phanHoi);

        List<LichSuXuLyLienHeAdminResponseDTO> lichSuList = lichSuRepository
                .findAllByPhanHoiIdDTO(saved.getId())
                .stream()
                .collect(Collectors.toList());

        return phanHoiMapper.toResponseDTO(saved, lichSuList);
    }

    @Transactional
    public void delete(UUID id) {
        if (!phanHoiRepository.existsById(id)) {
            throw new EntityNotFoundException("Phản hồi liên hệ không tồn tại");
        }
        phanHoiRepository.deleteById(id);
    }

    @Transactional
    public void deleteAll(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        phanHoiRepository.deleteAllByIdIn(ids);
    }

    public long countByTrangThai(TrangThaiXuLyLienHeEnum trangThai) {
        return phanHoiRepository.countByTrangThai(trangThai);
    }

    private void normalizeRequest(PhanHoiLienHeAdminRequestDTO request) {
        if (request == null) {
            throw new SimpleMessageException("Thông tin phản hồi liên hệ không được để trống");
        }
        request.setHoTen(request.getHoTen().trim());
        request.setEmail(request.getEmail().trim().toLowerCase());
        request.setChuDe(request.getChuDe().trim());
        request.setNoiDung(request.getNoiDung().trim());

        if (request.getSoDienThoai() != null) {
            request.setSoDienThoai(request.getSoDienThoai().trim());
            if (request.getSoDienThoai().isBlank()) {
                request.setSoDienThoai(null);
            }
        }
    }
}
