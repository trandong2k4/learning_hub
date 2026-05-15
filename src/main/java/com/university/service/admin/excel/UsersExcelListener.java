package com.university.service.admin.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.university.dto.request.admin.UsersAdminRequestDTO;
import com.university.dto.response.admin.ExcelImportResult;
import com.university.entity.Role;
import com.university.entity.UserRole;
import com.university.entity.Users;
import com.university.repository.admin.RoleAdminRepository;
import com.university.repository.admin.UserRoleAdminRepository;
import com.university.repository.admin.UsersAdminRepository;

import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

public class UsersExcelListener extends
        AnalysisEventListener<UsersAdminRequestDTO> {

    private final UsersAdminRepository usersRepository;
    private final UserRoleAdminRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    private final List<PendingUser> toSave = new ArrayList<>();
    private final List<String> errors = new ArrayList<>();

    private final Set<String> maUsersInFile = new HashSet<>();
    private final Set<String> maUsernameInDb;
    private final Map<String, Role> rolesByCode;

    private static final int BATCH_COUNT = 50;

    private int rowIndex = 1;
    private int successCount = 0;

    public UsersExcelListener(
            UsersAdminRepository usersRepository,
            RoleAdminRepository roleRepository,
            UserRoleAdminRepository userRoleRepository,
            PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.maUsernameInDb = new HashSet<>(usersRepository.findAllUserNames());
        this.rolesByCode = loadRolesByCode(roleRepository);
    }

    @Override
    public void invoke(UsersAdminRequestDTO data, AnalysisContext context) {
        rowIndex++;

        if (data == null || data.getUserName() == null ||
                data.getUserName().trim().isEmpty()) {
            errors.add("Dòng " + rowIndex + ": Mã Users không được để trống");
            return;
        }

        // Làm sạch dữ liệu
        String maUsers = data.getUserName().trim().toUpperCase(); // Giả sử mã trường viết hoa
        data.setUserName(maUsers);

        // === KIỂM TRA HỢP LỆ ===
        if (maUsers.length() > 30) {
            errors.add("Dòng " + rowIndex + ": Mã Users tối đa 30 ký tự");
            return;
        }

        // Kiểm tra trùng trong cùng file Excel
        if (maUsersInFile.contains(maUsers)) {
            errors.add("Dòng " + rowIndex + ": Mã Users '" + maUsers + "' bị trùng lặp trong file Excel");
            return;
        }
        maUsersInFile.add(maUsers);

        if (maUsernameInDb.contains(maUsers)) {
            errors.add("Dòng " + rowIndex + ": Mã Users '" + maUsers + "' đã tồn tại trong cơ sở dữ liệu");
            return;
        }

        Role role = null;
        if (data.getMaRole() != null && !data.getMaRole().trim().isEmpty()) {
            String maRole = data.getMaRole().trim().toUpperCase();
            role = rolesByCode.get(maRole);
            if (role == null) {
                errors.add("Dòng " + rowIndex + ": Mã vai trò '" + data.getMaRole().trim()
                        + "' không tồn tại, không tạo Users cho dòng này");
                return;
            }
        }

        // Chuyển sang Entity
        Users users = new Users();
        BeanUtils.copyProperties(data, users);

        // Mã hóa password trước khi lưu
        String rawPassword = users.getPassword();
        if (rawPassword != null && !rawPassword.isBlank()) {
            users.setPassWord(passwordEncoder.encode(rawPassword));
        }

        // Chuẩn hóa số điện thoại: thêm prefix "0" nếu là định dạng +84...
        String sdt = users.getSoDienThoai();
        if (sdt != null && sdt.startsWith("+84")) {
            users.setSoDienThoai("0" + sdt.substring(3));
        }
        toSave.add(new PendingUser(users, role));

        // Lưu batch
        if (toSave.size() >= BATCH_COUNT) {
            saveBatch();
        }
    }

    private void saveBatch() {
        if (!toSave.isEmpty()) {
            try {
                List<Users> usersToSave = toSave.stream()
                        .map(PendingUser::users)
                        .toList();
                usersRepository.saveAll(usersToSave);

                List<UserRole> userRoles = toSave.stream()
                        .filter(item -> item.role() != null)
                        .map(item -> {
                            UserRole userRole = new UserRole();
                            userRole.setUsers(item.users());
                            userRole.setRole(item.role());
                            return userRole;
                        })
                        .toList();
                if (!userRoles.isEmpty()) {
                    userRoleRepository.saveAll(userRoles);
                }

                // ✔ chỉ tăng khi save OK
                successCount += toSave.size();

                // ✔ update DB cache (tránh duplicate batch sau)
                toSave.forEach(n -> maUsernameInDb.add(n.users().getUsername()));

            } catch (Exception e) {
                errors.add("Lỗi khi lưu batch: " + e.getMessage());
            } finally {
                toSave.clear();
            }
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveBatch(); // Lưu nốt phần còn lại
    }

    // Phương thức trả về kết quả import
    public ExcelImportResult getResult() {
        ExcelImportResult result = new ExcelImportResult();
        result.setTotalRows(rowIndex - 1); // Trừ đi header
        result.setSuccessCount(successCount);
        // như chưa thành công
        result.setErrorCount(errors.size());
        result.setErrors(new ArrayList<>(errors));

        return result;
    }

    private Map<String, Role> loadRolesByCode(RoleAdminRepository roleRepository) {
        Map<String, Role> map = new HashMap<>();
        roleRepository.findAll().forEach(role -> {
            if (role.getMaRole() != null && !role.getMaRole().trim().isEmpty()) {
                map.put(role.getMaRole().trim().toUpperCase(), role);
            }
        });
        return map;
    }

    private record PendingUser(Users users, Role role) {
    }
}
