package com.university.service.admin;

import com.alibaba.excel.EasyExcel;
import com.university.dto.request.admin.UsersAdminRequestDTO;
import com.university.dto.response.admin.BatchDeleteResultDTO;
import com.university.dto.response.admin.BatchDeleteResultDTO.FailedUserDTO;
import com.university.dto.response.admin.ExcelImportResult;
import com.university.dto.response.admin.UsersAdminResponseDTO;
import com.university.dto.response.admin.UsersAdminResponseDTO.UserView;
import com.university.dto.response.auth.AuthResponseDTO;
import com.university.entity.Role;
import com.university.entity.UserRole;
import com.university.entity.Users;
import com.university.mapper.admin.UsersAdminMapper;
import com.university.repository.admin.HocVienAdminRepository;
import com.university.repository.admin.NhanVienAdminRepository;
import com.university.repository.admin.RoleAdminRepository;
import com.university.repository.admin.UserRoleAdminRepository;
import com.university.repository.admin.UsersAdminRepository;
import com.university.service.admin.excel.UsersExcelListener;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UsersAdminService {

    private final UsersAdminRepository usersAdminRepository;
    private final UsersAdminMapper usersMapper;
    private final HocVienAdminRepository hocVienAdminRepository;
    private final NhanVienAdminRepository nhanVienAdminRepository;
    private final RoleAdminRepository roleAdminRepository;
    private final UserRoleAdminRepository userRoleAdminRepository;
    private final PasswordEncoder passwordEncoder;

    public UsersAdminService(
            UsersAdminRepository usersRepository,
            UsersAdminMapper usersMapper,
            HocVienAdminRepository hocVienAdminRepository,
            NhanVienAdminRepository nhanVienAdminRepository,
            RoleAdminRepository roleAdminRepository,
            UserRoleAdminRepository userRoleAdminRepository,
            PasswordEncoder passwordEncoder) {
        this.usersAdminRepository = usersRepository;
        this.usersMapper = usersMapper;
        this.hocVienAdminRepository = hocVienAdminRepository;
        this.nhanVienAdminRepository = nhanVienAdminRepository;
        this.roleAdminRepository = roleAdminRepository;
        this.userRoleAdminRepository = userRoleAdminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Import Excel
    // ─────────────────────────────────────────────────────────────────────────

    public ExcelImportResult importFromExcel(MultipartFile file) throws java.io.IOException {
        UsersExcelListener listener = new UsersExcelListener(
                usersAdminRepository,
                roleAdminRepository,
                userRoleAdminRepository,
                passwordEncoder);
        EasyExcel.read(file.getInputStream(), UsersAdminRequestDTO.class, listener)
                .sheet("Users")
                .headRowNumber(1)
                .doRead();
        return listener.getResult();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Create
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public UsersAdminResponseDTO create(UsersAdminRequestDTO dto) {
        // Validate password (không có @NotBlank trong DTO vì update dùng chung)
        if (dto.getPassWord() == null || dto.getPassWord().isBlank()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }

        // Kiểm tra trùng lặp — throw 409 Conflict
        if (usersAdminRepository.existsByUserName(dto.getUserName())) {
            throw new IllegalStateException("Tên đăng nhập '" + dto.getUserName() + "' đã tồn tại");
        }
        if (dto.getEmail() != null && !dto.getEmail().isBlank()
                && usersAdminRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalStateException("Email '" + dto.getEmail() + "' đã được sử dụng");
        }
        if (usersAdminRepository.existsByCccd(dto.getCccd())) {
            throw new IllegalStateException("CCCD '" + dto.getCccd() + "' đã được sử dụng");
        }

        Role role = findRoleByMaRole(dto.getMaRole());
        Users user = usersMapper.toEntity(dto);
        Users saved = usersAdminRepository.save(user);
        if (role != null) {
            UserRole userRole = new UserRole();
            userRole.setUsers(saved);
            userRole.setRole(role);
            userRoleAdminRepository.save(userRole);
        }

        UsersAdminResponseDTO response = usersMapper.toResponseDTO(saved);
        List<String> roles = usersAdminRepository.findRolesByUserId(saved.getId());
        response.setRoles(roles);
        return response;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Read
    // ─────────────────────────────────────────────────────────────────────────

    public List<UsersAdminResponseDTO> getAll() {
        List<UsersAdminResponseDTO.UsersBasicProjection> projections = usersAdminRepository.findAllDTO();

        // Batch load all roles in ONE query to avoid N+1
        Map<UUID, List<String>> allRolesMap = new java.util.HashMap<>();
        List<Object[]> allRolesData = usersAdminRepository.findAllUserIdAndRoles();
        for (Object[] row : allRolesData) {
            UUID userId = (UUID) row[0];
            String maRole = (String) row[1];
            if (maRole != null) {
                allRolesMap.computeIfAbsent(userId, k -> new ArrayList<>()).add(maRole);
            }
        }

        List<UsersAdminResponseDTO> result = new ArrayList<>();
        for (UsersAdminResponseDTO.UsersBasicProjection p : projections) {
            UsersAdminResponseDTO dto = toDTO(p);
            dto.setRoles(allRolesMap.getOrDefault(dto.getId(), new ArrayList<>()));
            result.add(dto);
        }
        return result;
    }

    public UsersAdminResponseDTO getById(UUID id) {
        UsersAdminResponseDTO.UsersBasicProjection p = usersAdminRepository.findUsersById(id);
        if (p == null) {
            throw new EntityNotFoundException("Người dùng với id '" + id + "' không tồn tại");
        }
        UsersAdminResponseDTO dto = toDTO(p);
        List<String> roles = usersAdminRepository.findRolesByUserId(id);
        dto.setRoles(roles);
        return dto;
    }

    public UserView getByView(UUID id) {
        return usersAdminRepository.findByView(id);
    }

    public List<UsersAdminResponseDTO> getByHoTen(String hoTen) {
        List<UsersAdminResponseDTO.UsersBasicProjection> projections = usersAdminRepository.findUsersByHoTen(hoTen);

        // Batch load all roles in ONE query to avoid N+1
        Map<UUID, List<String>> allRolesMap = new java.util.HashMap<>();
        List<Object[]> allRolesData = usersAdminRepository.findAllUserIdAndRoles();
        for (Object[] row : allRolesData) {
            UUID userId = (UUID) row[0];
            String maRole = (String) row[1];
            if (maRole != null) {
                allRolesMap.computeIfAbsent(userId, k -> new ArrayList<>()).add(maRole);
            }
        }

        List<UsersAdminResponseDTO> result = new ArrayList<>();
        for (UsersAdminResponseDTO.UsersBasicProjection p : projections) {
            UsersAdminResponseDTO dto = toDTO(p);
            dto.setRoles(allRolesMap.getOrDefault(dto.getId(), new ArrayList<>()));
            result.add(dto);
        }
        return result;
    }

    public UsersAdminResponseDTO getByUserName(String userName) {
        UsersAdminResponseDTO.UsersBasicProjection p = usersAdminRepository.findByUserNameDTO(userName);
        if (p == null) return null;
        UsersAdminResponseDTO dto = toDTO(p);
        UUID userId = p.getId();
        List<String> roles = usersAdminRepository.findRolesByUserId(userId);
        dto.setRoles(roles);
        return dto;
    }

    public List<AuthResponseDTO> dSNameRoleUSers(UUID id) {
        return usersAdminRepository.findAllRoleAndPermissionsByUserId(id);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Update
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public UsersAdminResponseDTO update(UUID id, UsersAdminRequestDTO dto) {
        Users user = usersAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Người dùng với id '" + id + "' không tồn tại"));

        // Kiểm tra trùng lặp (loại trừ chính user đang sửa) — throw 409 Conflict
        if (usersAdminRepository.existsByUserNameAndIdNot(dto.getUserName(), id)) {
            throw new IllegalStateException("Tên đăng nhập '" + dto.getUserName() + "' đã tồn tại");
        }
        if (dto.getEmail() != null && !dto.getEmail().isBlank()
                && usersAdminRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
            throw new IllegalStateException("Email '" + dto.getEmail() + "' đã được sử dụng");
        }
        if (usersAdminRepository.existsByCccdAndIdNot(dto.getCccd(), id)) {
            throw new IllegalStateException("CCCD '" + dto.getCccd() + "' đã được sử dụng");
        }

        usersMapper.updateEntity(user, dto);
        usersAdminRepository.save(user);
        UsersAdminResponseDTO response = usersMapper.toResponseDTO(user);
        List<String> roles = usersAdminRepository.findRolesByUserId(id);
        response.setRoles(roles);
        return response;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Delete
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public void delete(UUID id) {
        Users user = usersAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Người dùng với id '" + id + "' không tồn tại"));
        checkCanDelete(user.getId());
        usersAdminRepository.delete(user);
    }

    @Transactional
    public BatchDeleteResultDTO deleteAllByList(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) return BatchDeleteResultDTO.success(0);

        Map<UUID, Users> userMap = usersAdminRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Users::getId, Function.identity()));
        Set<UUID> isHocVien = new HashSet<>(hocVienAdminRepository.findUserIdsAssignedToHocVien(ids));
        Set<UUID> isNhanVien = new HashSet<>(nhanVienAdminRepository.findUserIdsAssignedToNhanVien(ids));

        List<UUID> deletableIds = new ArrayList<>();
        List<FailedUserDTO> failedUsers = new ArrayList<>();

        for (UUID id : ids) {
            if (!userMap.containsKey(id)) {
                failedUsers.add(FailedUserDTO.builder().id(id).hoTen(null).reason("Người dùng không tồn tại").build());
                continue;
            }
            String hoTen = userMap.get(id).getHoTen();
            String reason = null;
            if (isHocVien.contains(id)) reason = "Người dùng đang là học viên. Vui lòng xóa hồ sơ học viên trước.";
            else if (isNhanVien.contains(id)) reason = "Người dùng đang là nhân viên. Vui lòng xóa hồ sơ nhân viên trước.";

            if (reason != null) failedUsers.add(FailedUserDTO.builder().id(id).hoTen(hoTen).reason(reason).build());
            else deletableIds.add(id);
        }

        if (!deletableIds.isEmpty()) {
            usersAdminRepository.deleteAllByIdIn(deletableIds);
        }

        return BatchDeleteResultDTO.partial(ids.size(), deletableIds.size(), failedUsers.size(), failedUsers);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private void checkCanDelete(UUID userId) {
        if (hocVienAdminRepository.existsByUsersId(userId)) {
            throw new IllegalStateException(
                    "Không thể xóa: người dùng đang là học viên. Vui lòng xóa hồ sơ học viên trước");
        }
        if (nhanVienAdminRepository.existsByUsersId(userId)) {
            throw new IllegalStateException(
                    "Không thể xóa: người dùng đang là nhân viên. Vui lòng xóa hồ sơ nhân viên trước");
        }
    }

    private Role findRoleByMaRole(String maRole) {
        if (maRole == null || maRole.trim().isEmpty()) {
            return null;
        }

        String normalized = maRole.trim();
        return roleAdminRepository.findFirstByMaRoleIgnoreCase(normalized)
                .orElseThrow(() -> new IllegalArgumentException("Vai trò '" + normalized + "' không tồn tại"));
    }

    private UsersAdminResponseDTO toDTO(UsersAdminResponseDTO.UsersBasicProjection p) {
        UsersAdminResponseDTO dto = new UsersAdminResponseDTO();
        dto.setId(p.getId());
        dto.setUserName(p.getUserName());
        dto.setPassWord(p.getPassWord());
        dto.setEmail(p.getEmail());
        dto.setCccd(p.getCccd());
        dto.setHoTen(p.getHoTen());
        dto.setDiaChi(p.getDiaChi());
        dto.setGioiTinh(p.getGioiTinh());
        dto.setNgaySinh(p.getNgaySinh());
        dto.setSoDienThoai(p.getSoDienThoai());
        dto.setTrangThai(p.getTrangThai());
        dto.setGhiChu(p.getGhiChu());
        dto.setCreateAt(p.getCreateAt());
        dto.setUpdateAt(p.getUpdateAt());
        return dto;
    }
}
