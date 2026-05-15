package com.university.service.lecturer;

import com.university.dto.request.lecturer.DocumentRequestDTO;
import com.university.dto.response.lecturer.DocumentResponseDTO;
import com.university.entity.LopHocPhan;
import com.university.entity.TaiLieu;
import com.university.enums.TaiLieuEnum;
import com.university.repository.admin.LopHocPhanAdminRepository;
import com.university.repository.lecturer.LecturerDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LecturerDocumentService {

    private final LecturerDocumentRepository documentRepository;
    private final LopHocPhanAdminRepository lopHocPhanRepository;
    private final LecturerValidationService validationService;

    public List<DocumentResponseDTO> getDocuments(UUID lopHocPhanId, UUID userId) {
        validationService.validateLecturerAssignment(userId, lopHocPhanId);
        return documentRepository.findByLopHocPhan_Id(lopHocPhanId).stream()
                .map(t -> new DocumentResponseDTO(t.getId(), t.getTenTaiLieu(), t.getMoTa(),
                        t.getFileTaiLieuUrl(), t.getLoaiTaiLieu().name(), t.getNgayDang(), t.getLopHocPhan().getId()))
                .collect(Collectors.toList());
    }

    public DocumentResponseDTO createDocument(UUID userId, DocumentRequestDTO request) {
        validationService.validateLecturerAssignment(userId, request.getLopHocPhanId());
        LopHocPhan lopHocPhan = lopHocPhanRepository.findById(request.getLopHocPhanId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học phần."));

        TaiLieu taiLieu = new TaiLieu();
        taiLieu.setTenTaiLieu(request.getTenTaiLieu());
        taiLieu.setMoTa(request.getMoTa());
        taiLieu.setFileTaiLieuUrl(request.getFileTaiLieuUrl());
        taiLieu.setLopHocPhan(lopHocPhan);
        taiLieu.setLoaiTaiLieu(detectFileType(request.getFileTaiLieuUrl(), request.getLoaiTaiLieu()));
        taiLieu.setNgayDang(LocalDateTime.now());

        TaiLieu saved = documentRepository.save(taiLieu);
        return new DocumentResponseDTO(saved.getId(), saved.getTenTaiLieu(), saved.getMoTa(),
                saved.getFileTaiLieuUrl(), saved.getLoaiTaiLieu().name(), saved.getNgayDang(), saved.getLopHocPhan().getId());
    }

    private TaiLieuEnum detectFileType(String fileUrl, String providedType) {
        if (providedType != null && !providedType.isBlank()) {
            try {
                return TaiLieuEnum.valueOf(providedType.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ignored) {}
        }
        if (fileUrl == null || fileUrl.isBlank()) return TaiLieuEnum.OTHER;
        try {
            String path = new URI(fileUrl).getPath();
            String[] parts = path.split("\\.");
            String ext = parts[parts.length - 1].toLowerCase(Locale.ROOT);
            return switch (ext) {
                case "pdf" -> TaiLieuEnum.PDF;
                case "doc", "docx" -> TaiLieuEnum.DOCX;
                case "ppt", "pptx", "odp" -> TaiLieuEnum.PPTX;
                case "xls", "xlsx" -> TaiLieuEnum.XLSX;
                case "zip", "rar" -> TaiLieuEnum.ZIP;
                case "mp4", "avi", "mov", "mkv" -> TaiLieuEnum.VIDEO;
                default -> TaiLieuEnum.OTHER;
            };
        } catch (Exception ignored) {
            return TaiLieuEnum.OTHER;
        }
    }

    public DocumentResponseDTO updateDocument(UUID userId, UUID documentId, DocumentRequestDTO request) {
        TaiLieu existing = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Tài liệu không tồn tại."));
        validationService.validateLecturerAssignment(userId, existing.getLopHocPhan().getId());

        existing.setTenTaiLieu(request.getTenTaiLieu());
        existing.setMoTa(request.getMoTa());
        existing.setFileTaiLieuUrl(request.getFileTaiLieuUrl());
        existing.setLoaiTaiLieu(detectFileType(request.getFileTaiLieuUrl(), request.getLoaiTaiLieu()));

        TaiLieu saved = documentRepository.save(existing);
        return new DocumentResponseDTO(saved.getId(), saved.getTenTaiLieu(), saved.getMoTa(),
                saved.getFileTaiLieuUrl(), saved.getLoaiTaiLieu().name(), saved.getNgayDang(), saved.getLopHocPhan().getId());
    }

    public void deleteDocument(UUID userId, UUID documentId) {
        TaiLieu existing = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Tài liệu không tồn tại."));
        validationService.validateLecturerAssignment(userId, existing.getLopHocPhan().getId());
        documentRepository.delete(existing);
    }
}
