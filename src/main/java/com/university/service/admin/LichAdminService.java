package com.university.service.admin;

import com.alibaba.excel.EasyExcel;
import com.university.dto.request.admin.GioHocAdminRequestDTO;
import com.university.dto.request.admin.LichAdminRequestDTO;
import com.university.dto.request.admin.PhongAdminRequestDTO;
import com.university.dto.response.admin.ExcelImportResult;
import com.university.dto.response.admin.GioHocAdminResponseDTO;
import com.university.dto.response.admin.LichAdminResponseDTO;
import com.university.dto.response.admin.PhongAdminResponseDTO;
import com.university.entity.GioHoc;
import com.university.entity.Lich;
import com.university.entity.LopHocPhan;
import com.university.entity.Phong;
import com.university.exception.SimpleMessageException;
import com.university.mapper.admin.GioHocAdminMapper;
import com.university.mapper.admin.LichAdminMapper;
import com.university.mapper.admin.PhongAdminMapper;
import com.university.repository.admin.GioHocAdminRepository;
import com.university.repository.admin.LichAdminRepository;
import com.university.repository.admin.LopHocPhanAdminRepository;
import com.university.repository.admin.PhongAdminRepository;
import com.university.service.admin.excel.GioHocExcelListener;
import com.university.service.admin.excel.PhongExcelListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LichAdminService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final LichAdminRepository lichAdminRepository;
    private final GioHocAdminRepository gioHocAdminRepository;
    private final PhongAdminRepository phongAdminRepository;
    private final LopHocPhanAdminRepository lopHocPhanAdminRepository;
    private final LichAdminMapper lichMapper;
    private final PhongAdminMapper phongMapper;
    private final GioHocAdminMapper gioHocMapper;

    // ==================== LICH HOC ====================

    @Transactional
    public LichAdminResponseDTO createLich(LichAdminRequestDTO request) {
        if (request == null) {
            throw new SimpleMessageException("Dữ liệu không hợp lệ");
        }

        GioHoc gioHoc = gioHocAdminRepository.findById(request.getGioHocId())
                .orElseThrow(() -> new SimpleMessageException("Giờ học không tồn tại"));

        Phong phong = phongAdminRepository.findById(request.getPhongId())
                .orElseThrow(() -> new SimpleMessageException("Phòng học không tồn tại"));

        LopHocPhan lopHocPhan = lopHocPhanAdminRepository.findById(request.getLopHocPhanId())
                .orElseThrow(() -> new SimpleMessageException("Lớp học phần không tồn tại"));

        validateRoomScheduleAvailable(null, request, phong, gioHoc);

        List<Lich> existingSchedules = lichAdminRepository.findAllLichByLopHocPhanId(lopHocPhan.getId());

        for (Lich existing : existingSchedules) {
            if (existing.getNgayHoc().equals(request.getNgayHoc())) {
                if (existing.getGioHoc().getId().equals(request.getGioHocId())) {
                    throw new SimpleMessageException("Lớp học phần này đã có lịch vào ngày "
                            + request.getNgayHoc() + " tại khung giờ này!");
                }
            }
        }

        Lich lich = lichMapper.toEntity(request);
        lich.setGioHoc(gioHoc);
        lich.setPhong(phong);
        lich.setLopHocPhan(lopHocPhan);
        lich.setCreatedAt(LocalDateTime.now());
        lich.setUpdatedAt(LocalDateTime.now());

        Lich saved = lichAdminRepository.save(lich);
        return lichMapper.toResponseDTO(saved);
    }

    @Transactional
    public LichAdminResponseDTO updateLich(UUID id, LichAdminRequestDTO request) {
        Lich existing = lichAdminRepository.findById(id)
                .orElseThrow(() -> new SimpleMessageException("Lịch không tồn tại"));

        GioHoc gioHoc = gioHocAdminRepository.findById(request.getGioHocId())
                .orElseThrow(() -> new SimpleMessageException("Giờ học không tồn tại"));

        Phong phong = phongAdminRepository.findById(request.getPhongId())
                .orElseThrow(() -> new SimpleMessageException("Phòng học không tồn tại"));

        LopHocPhan lopHocPhan = lopHocPhanAdminRepository.findById(request.getLopHocPhanId())
                .orElseThrow(() -> new SimpleMessageException("Lớp học phần không tồn tại"));

        validateRoomScheduleAvailable(id, request, phong, gioHoc);

        lichMapper.updateEntity(existing, request);
        existing.setGioHoc(gioHoc);
        existing.setPhong(phong);
        existing.setLopHocPhan(lopHocPhan);
        existing.setUpdatedAt(LocalDateTime.now());

        Lich updated = lichAdminRepository.save(existing);
        return lichMapper.toResponseDTO(updated);
    }

    private void validateRoomScheduleAvailable(UUID excludeLichId, LichAdminRequestDTO request, Phong phong,
            GioHoc gioHoc) {
        LocalDateTime startOfDay = request.getNgayHoc().toLocalDate().atStartOfDay();
        LocalDateTime startOfNextDay = startOfDay.plusDays(1);

        List<Lich> conflicts = excludeLichId == null
                ? lichAdminRepository.findRoomScheduleConflicts(
                        phong.getId(),
                        gioHoc.getId(),
                        startOfDay,
                        startOfNextDay)
                : lichAdminRepository.findRoomScheduleConflictsExcluding(
                        phong.getId(),
                        gioHoc.getId(),
                        startOfDay,
                        startOfNextDay,
                        excludeLichId);

        if (conflicts.isEmpty()) {
            return;
        }

        Lich conflict = conflicts.get(0);
        String maLopHocPhan = conflict.getLopHocPhan() != null ? conflict.getLopHocPhan().getMaLopHocPhan() : "khác";
        throw new SimpleMessageException("Phòng " + phong.getMaPhong()
                + " đã có lịch học của lớp " + maLopHocPhan
                + " vào ngày " + request.getNgayHoc().format(DATE_FORMATTER)
                + " trong khung giờ " + gioHoc.getTenGioHoc());
    }

    @Transactional(readOnly = true)
    public LichAdminResponseDTO getLichById(UUID id) {
        Lich lich = lichAdminRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new SimpleMessageException("Lịch không tồn tại"));
        return lichMapper.toResponseDTO(lich);
    }

    @Transactional(readOnly = true)
    public List<LichAdminResponseDTO> getAllLich() {
        return lichAdminRepository.findAllWithDetails()
                .stream()
                .map(lichMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LichAdminResponseDTO> getAllLichByLopHopPhan(UUID id) {
        List<Lich> lichs = lichAdminRepository.findAllLichByLopHocPhanId(id);
        return lichs.stream().map(lichMapper::toResponseDTO).toList();
    }

    @Transactional
    public void delete(UUID id) {
        if (!lichAdminRepository.existsById(id)) {
            throw new SimpleMessageException("Lịch không tồn tại");
        }
        lichAdminRepository.deleteById(id);
    }

    @Transactional
    public List<String> deleteAllByList(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        List<UUID> deletable = new java.util.ArrayList<>();
        List<String> errors = new java.util.ArrayList<>();

        for (UUID id : ids) {
            if (lichAdminRepository.existsById(id)) {
                deletable.add(id);
            } else {
                errors.add("ID không tồn tại: " + id);
            }
        }

        if (!deletable.isEmpty()) {
            try {
                lichAdminRepository.deleteAllByIdIn(deletable);
            } catch (Exception e) {
                throw new SimpleMessageException("Lỗi khi xóa danh sách: " + e.getMessage());
            }
        }

        return errors;
    }

    // ==================== PHONG HOC ====================

    public ExcelImportResult importPhongFromExcel(MultipartFile file) throws IOException {
        PhongExcelListener listener = new PhongExcelListener(phongAdminRepository);
        EasyExcel.read(file.getInputStream(), PhongExcelListener.PhongExcelRow.class, listener)
                .sheet("Phong")
                .headRowNumber(1)
                .doRead();
        return listener.getResult();
    }

    private void checkPhongCanDelete(UUID id) {
        if (!phongAdminRepository.existsById(id)) {
            throw new SimpleMessageException("Phòng không tồn tại");
        }
        if (lichAdminRepository.existsByPhongId(id)) {
            throw new SimpleMessageException("Phòng đang được sử dụng trong lịch học, không thể xóa");
        }
    }

    @Transactional
    public PhongAdminResponseDTO createPhong(PhongAdminRequestDTO request) {
        if (phongAdminRepository.findAll().stream()
                .anyMatch(p -> p.getMaPhong().equalsIgnoreCase(request.getMaPhong()))) {
            throw new SimpleMessageException("Mã phòng đã tồn tại");
        }
        Phong phong = phongMapper.toEntity(request);
        return phongMapper.toResponseDTO(phongAdminRepository.save(phong));
    }

    @Transactional
    public String createPhongList(List<PhongAdminRequestDTO> request) {
        if (request == null || request.isEmpty()) {
            return "Danh sách rỗng";
        }
        List<Phong> phongs = request.stream().map(phongMapper::toEntity).toList();
        phongAdminRepository.saveAll(phongs);
        return "Thêm danh sách thành công";
    }

    @Transactional(readOnly = true)
    public PhongAdminResponseDTO getPhongById(UUID id) {
        Phong phong = phongAdminRepository.findById(id)
                .orElseThrow(() -> new SimpleMessageException("Phòng không tồn tại"));
        return phongMapper.toResponseDTO(phong);
    }

    @Transactional(readOnly = true)
    public List<PhongAdminResponseDTO> getAllPhong() {
        return phongAdminRepository.findAllWithLichs().stream()
                .map(phongMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public PhongAdminResponseDTO updatePhong(UUID id, PhongAdminRequestDTO request) {
        Phong phong = phongAdminRepository.findById(id)
                .orElseThrow(() -> new SimpleMessageException("Phòng không tồn tại"));
        phong = phongMapper.updateEntity(phong, request);
        phongAdminRepository.save(phong);
        return phongMapper.toResponseDTO(phong);
    }

    @Transactional
    public void deletePhong(UUID id) {
        checkPhongCanDelete(id);
        phongAdminRepository.deleteById(id);
    }

    @Transactional
    public void deletePhongAllByList(List<UUID> ids) {
        if (ids == null || ids.isEmpty())
            return;
        for (UUID id : ids) {
            checkPhongCanDelete(id);
        }
        try {
            phongAdminRepository.deleteAllByIdIn(ids);
        } catch (Exception e) {
            throw new SimpleMessageException("Lỗi khi xóa danh sách: " + e.getMessage());
        }
    }

    // ==================== GIO HOC ====================

    @Transactional(readOnly = true)
    public List<GioHocAdminResponseDTO> getAllGioHoc() {
        return gioHocAdminRepository.findAllDTO();
    }

    @Transactional(readOnly = true)
    public GioHocAdminResponseDTO getGioHocById(UUID id) {
        return gioHocAdminRepository.findDTOById(id);
    }

    @Transactional(readOnly = true)
    public List<GioHocAdminResponseDTO> searchGioHocByTenGioHoc(String key) {
        return gioHocAdminRepository.searchByTenGioHoc(key);
    }

    @Transactional
    public GioHocAdminResponseDTO createGioHoc(GioHocAdminRequestDTO dto) {
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
    public GioHocAdminResponseDTO updateGioHoc(UUID id, GioHocAdminRequestDTO dto) {
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
    public void deleteGioHoc(UUID id) {
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
    public void deleteGioHocAllByList(List<UUID> ids) {
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

    public ExcelImportResult importGioHocFromExcel(MultipartFile file) throws IOException {
        GioHocExcelListener listener = new GioHocExcelListener(gioHocAdminRepository);
        EasyExcel.read(file.getInputStream(), GioHocAdminRequestDTO.class, listener).sheet().doRead();
        return listener.getResult();
    }
}
