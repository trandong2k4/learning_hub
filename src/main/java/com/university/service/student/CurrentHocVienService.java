package com.university.service.student;

import com.university.config.SecurityUtils;
import com.university.entity.HocVien;
import com.university.exception.SimpleMessageException;
import com.university.repository.student.HocVienStudentsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service tiện ích cung cấp các phương thức lấy thông tin học viên đang đăng nhập.
 *
 * <p>Tất cả phương thức đều đọc {@code userId} từ Spring Security Context
 * và tra cứu entity {@link HocVien} tương ứng.
 * Ném {@link com.university.exception.SimpleMessageException} nếu không tìm thấy.</p>
 */
@Transactional
@Service
@RequiredArgsConstructor
public class CurrentHocVienService {

    private final HocVienStudentsRepository hocVienRepository;

    /**
     * Trả về UUID của học viên đang đăng nhập.
     *
     * @return ID của học viên
     */
    public UUID getCurrentHocVienId() {
        return getCurrentHocVien().getId();
    }

    /**
     * Trả về entity {@link HocVien} của học viên đang đăng nhập.
     *
     * <p>Không eager-load quan hệ {@code nganh}. Dùng khi chỉ cần dữ liệu cơ bản của học viên.</p>
     *
     * @return entity HocVien tương ứng với user đang đăng nhập
     */
    public HocVien getCurrentHocVien() {
        UUID userId = SecurityUtils.getCurrentUserId();
        return hocVienRepository.findByUsers_Id(userId)
                .orElseThrow(() -> new SimpleMessageException("Khong tim thay hoc vien"));
    }

    /**
     * Trả về entity {@link HocVien} kèm thông tin ngành ({@code JOIN FETCH nganh}).
     *
     * <p>Dùng khi nghiệp vụ cần truy cập thông tin ngành học để tránh LazyInitializationException.</p>
     *
     * @return entity HocVien đã fetch sẵn quan hệ nganh và users
     */
    public HocVien getCurrentHocVienWithNganh() {
        UUID userId = SecurityUtils.getCurrentUserId();
        return hocVienRepository.findByUsersIdWithNganh(userId)
                .orElseThrow(() -> new SimpleMessageException("Khong tim thay hoc vien"));
    }

    /**
     * Trả về entity {@link HocVien} với pessimistic write lock để cập nhật an toàn.
     *
     * <p>Sử dụng {@link jakarta.persistence.LockModeType#PESSIMISTIC_WRITE} nhằm ngăn
     * các transaction khác đọc/ghi bản ghi cùng lúc trong quá trình cập nhật.</p>
     *
     * @return entity HocVien được khóa ở mức database row
     */
    public HocVien getCurrentHocVienForUpdate() {
        UUID userId = SecurityUtils.getCurrentUserId();
        return hocVienRepository.findByUsersIdForUpdate(userId)
                .orElseThrow(() -> new SimpleMessageException("Khong tim thay hoc vien"));
    }

    /**
     * Trả về entity {@link HocVien} với pessimistic write lock, kèm JOIN FETCH {@code users} và {@code nganh}.
     *
     * <p>Dùng khi cần cập nhật entity và build response DTO ngay từ entity đã load,
     * tránh SELECT thêm sau khi update.</p>
     *
     * @return entity HocVien được khóa, đã fetch sẵn users và nganh
     */
    public HocVien getCurrentHocVienForUpdateWithRelations() {
        UUID userId = SecurityUtils.getCurrentUserId();
        return hocVienRepository.findByUsersIdForUpdateWithRelations(userId)
                .orElseThrow(() -> new SimpleMessageException("Khong tim thay hoc vien"));
    }
}
    