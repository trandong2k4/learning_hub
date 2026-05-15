package com.university.repository.student;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.university.entity.HocVien;

import jakarta.persistence.LockModeType;

/**
 * Repository cung cấp các truy vấn entity {@link HocVien} dùng trong nghiệp vụ học viên.
 *
 * <p>Bao gồm các phương thức:</p>
 * <ul>
 *   <li>Tải kèm ngành học ({@code JOIN FETCH}) để tránh N+1 query và LazyInitializationException.</li>
 *   <li>Lấy entity với pessimistic write lock cho các thao tác cập nhật đồng thời.</li>
 * </ul>
 */
@Repository
public interface HocVienStudentsRepository extends JpaRepository<HocVien, UUID> {

    /**
     * Tìm học viên theo ID học viên, eager-load ngành và tài khoản người dùng.
     *
     * @param hocVienId UUID của entity HocVien
     * @return Optional chứa HocVien kèm nganh và users
     */
    @Query("""
        SELECT h
        FROM HocVien h
        JOIN FETCH h.nganh n
        JOIN FETCH h.users u
        WHERE h.id = :hocVienId
    """)
    Optional<HocVien> findByIdWithNganh(@Param("hocVienId") UUID hocVienId);

    /**
     * Tìm học viên theo ID người dùng, eager-load ngành và tài khoản người dùng.
     *
     * @param userId UUID của {@code Users} liên kết
     * @return Optional chứa HocVien kèm nganh và users
     */
    @Query("""
        SELECT h
        FROM HocVien h
        JOIN FETCH h.nganh n
        JOIN FETCH h.users u
        WHERE u.id = :userId
    """)
    Optional<HocVien> findByUsersIdWithNganh(@Param("userId") UUID userId);

    /**
     * Tìm học viên theo ID học viên với pessimistic write lock.
     *
     * <p>Dùng khi cần cập nhật dữ liệu học viên, khóa bản ghi để tránh dirty write.</p>
     *
     * @param hocVienId UUID của entity HocVien
     * @return Optional chứa HocVien đang được khóa
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT h
        FROM HocVien h
        WHERE h.id = :hocVienId
    """)
    Optional<HocVien> findByIdForUpdate(@Param("hocVienId") UUID hocVienId);

    /**
     * Tìm học viên theo ID người dùng (không lock, không fetch thêm quan hệ).
     *
     * @param userId UUID của {@code Users}
     * @return Optional chứa entity HocVien
     */
    Optional<HocVien> findByUsers_Id(UUID userId);

    /**
     * Tìm học viên theo ID người dùng với pessimistic write lock.
     *
     * <p>Dùng trong {@link com.university.service.student.CurrentHocVienService#getCurrentHocVienForUpdate()}
     * khi học viên tự cập nhật hồ sơ của mình.</p>
     *
     * @param userId UUID của {@code Users} đang đăng nhập
     * @return Optional chứa HocVien đang được khóa
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT h
        FROM HocVien h
        WHERE h.users.id = :userId
    """)
    Optional<HocVien> findByUsersIdForUpdate(@Param("userId") UUID userId);

    /**
     * Tìm học viên theo ID người dùng với pessimistic write lock,
     * đồng thời JOIN FETCH {@code users} và {@code nganh} trong một query.
     *
     * <p>Dùng khi cần cập nhật và ngay sau đó build response DTO từ entity
     * mà không cần thêm SELECT nào. Kết hợp lock + eager-load để tối ưu.</p>
     *
     * @param userId UUID của {@code Users} đang đăng nhập
     * @return Optional chứa HocVien được khóa, kèm users và nganh đã load
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT h
        FROM HocVien h
        JOIN FETCH h.users u
        LEFT JOIN FETCH h.nganh n
        WHERE u.id = :userId
    """)
    Optional<HocVien> findByUsersIdForUpdateWithRelations(@Param("userId") UUID userId);
}
