package com.university.service.admin;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.StringUtils;
import com.university.dto.request.admin.MonHocAdminRequestDTO;
import com.university.dto.response.admin.ExcelImportResult;
import com.university.dto.response.admin.MonHocAdminResponseDTO;
import com.university.entity.MonHoc;
import com.university.entity.LopHocPhan;
import com.university.entity.ChuongTrinhDaoTao;
import com.university.entity.MonHocTienQuyet;
import com.university.exception.SimpleMessageException;
import com.university.mapper.admin.MonHocAdminMapper;
import com.university.repository.admin.MonHocAdminRepository;
import com.university.repository.admin.LopHocPhanAdminRepository;
import com.university.repository.admin.ChuongTrinhDaoTaoAdminRepository;
import com.university.repository.admin.MonHocTienQuyetAdminRepository;
import com.university.service.admin.excel.MonHocExcelListener;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MonHocAdminService {
    private final MonHocAdminMapper monHocAdminMapper;
    private final MonHocAdminRepository monHocAdminRepository;
    private final LopHocPhanAdminRepository lopHocPhanRepository;
    private final ChuongTrinhDaoTaoAdminRepository chuongTrinhDaoTaoRepository;
    private final MonHocTienQuyetAdminRepository monHocTienQuyetRepository;

    public ExcelImportResult importFromExcel(MultipartFile file) throws java.io.IOException {
        MonHocExcelListener listener = new MonHocExcelListener(monHocAdminRepository);

        EasyExcel.read(file.getInputStream(), MonHocAdminRequestDTO.class, listener)
                .sheet("MonHoc")
                .headRowNumber(1)
                .doRead();

        return listener.getResult();
    }

    public MonHocAdminResponseDTO create(MonHocAdminRequestDTO dto) {
        try {
            if (StringUtils.isBlank(dto.getMaMonHoc())) {
                throw new SimpleMessageException("Mã môn học không được để trống");
            }
            if (StringUtils.isBlank(dto.getTenMonHoc())) {
                throw new SimpleMessageException("Tên môn học không được để trống");
            }

            if (monHocAdminRepository.existsByMaMonHoc(dto.getMaMonHoc()))
                throw new SimpleMessageException("Mã quyền '" + dto.getMaMonHoc() + "' đã tồn tại!");

            return monHocAdminMapper.toResponseDTO(monHocAdminRepository.save(monHocAdminMapper.toEntity(dto)));

        } catch (Exception e) {
            throw new SimpleMessageException("Thêm môn học không thành công!");
        }
    }

    public List<MonHocAdminResponseDTO> getALLMonHOCDTO() {
        return monHocAdminRepository.FindAllDTO();
    }

    public MonHocAdminResponseDTO getMonHocById(UUID id) {
        MonHocAdminResponseDTO monHoc = monHocAdminRepository.findMonHocById(id);
        if (monHoc == null) {
            throw new EntityNotFoundException("Không tìm thấy môn học");
        }
        return monHoc;
    }

    public List<MonHocAdminResponseDTO> getMonHocByName(String keyword) {
        return monHocAdminRepository.findMonHocByTen(keyword);
    }

    public MonHocAdminResponseDTO updateMonHoc(UUID id, MonHocAdminRequestDTO request) {
        MonHoc monHoc = monHocAdminRepository.findById(id)
                .orElseThrow(() -> new SimpleMessageException("Môn học không tồn tại"));

        if (!monHoc.getMaMonHoc().equals(request.getMaMonHoc()) && monHocAdminRepository.existsByMaMonHoc(request.getMaMonHoc())) {
            throw new SimpleMessageException("Mã môn học '" + request.getMaMonHoc() + "' đã tồn tại!");
        }

        monHoc = monHocAdminMapper.updateEntity(monHoc, request);
        monHocAdminRepository.save(monHoc);
        return monHocAdminMapper.toResponseDTO(monHoc);
    }

    @Transactional
    public void delete(UUID monhocId) {
        MonHoc monHoc = monHocAdminRepository.findById(monhocId)
                .orElseThrow(() -> new SimpleMessageException("Môn học không tồn tại"));

        List<LopHocPhan> lopHocPhans = lopHocPhanRepository.findAllByMonHocId(monhocId);
        if (!lopHocPhans.isEmpty()) {
            throw new SimpleMessageException("Môn học '" + monHoc.getTenMonHoc() + "' vẫn còn " + lopHocPhans.size() + " lớp học phần. Vui lòng xóa lớp học phần trước.");
        }

        List<ChuongTrinhDaoTao> chuongTrinhs = chuongTrinhDaoTaoRepository.findAllByMonHocId(monhocId);
        if (!chuongTrinhs.isEmpty()) {
            chuongTrinhDaoTaoRepository.deleteAll(chuongTrinhs);
        }

        List<MonHocTienQuyet> tienQuyets = monHocTienQuyetRepository.findAllByMonHocId(monhocId);
        if (!tienQuyets.isEmpty()) {
            monHocTienQuyetRepository.deleteAll(tienQuyets);
        }

        List<MonHocTienQuyet> asTienQuyet = monHocTienQuyetRepository.findAllByMonTienQuyetId(monhocId);
        if (!asTienQuyet.isEmpty()) {
            monHocTienQuyetRepository.deleteAll(asTienQuyet);
        }

        monHocAdminRepository.delete(monHoc);
    }

    @Transactional
    public void deleteAllByList(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        try {
            for (UUID uuid : ids) {
                MonHoc monHoc = monHocAdminRepository.findById(uuid)
                        .orElseThrow(() -> new SimpleMessageException("Môn học không tồn tại"));

                if (lopHocPhanRepository.existsByMonHocId(uuid)) {
                    throw new SimpleMessageException("Môn học '" + monHoc.getTenMonHoc() + "' vẫn còn lớp học phần");
                }
            }
            monHocAdminRepository.deleteAllByIdIn(ids);
        } catch (SimpleMessageException e) {
            throw e;
        } catch (Exception e) {
            throw new SimpleMessageException("Lỗi khi xóa danh sách: " + e.getMessage());
        }
    }

}