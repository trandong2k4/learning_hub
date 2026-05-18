package com.university.service.admin.excel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.university.dto.request.admin.HocVienExcelDTO;
import com.university.dto.response.admin.ExcelImportResult;
import com.university.entity.HocVien;
import com.university.entity.Nganh;
import com.university.entity.Role;
import com.university.entity.UserRole;
import com.university.entity.Users;
import com.university.enums.GioiTinhEnum;
import com.university.repository.admin.HocVienAdminRepository;
import com.university.repository.admin.RoleAdminRepository;
import com.university.repository.admin.UserRoleAdminRepository;
import com.university.repository.admin.UsersAdminRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

public class HocVienExcelListener extends AnalysisEventListener<HocVienExcelDTO> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final HocVienAdminRepository hocVienAdminRepository;
    private final UsersAdminRepository usersAdminRepository;
    private final RoleAdminRepository roleAdminRepository;
    private final UserRoleAdminRepository userRoleAdminRepository;
    private final PasswordEncoder passwordEncoder;

    private final List<PendingHocVien> toSave = new ArrayList<>();
    private final List<String> errors = new ArrayList<>();

    private final Set<String> maHocVienInDb;
    private final Set<String> usernamesInDb;
    private final Set<String> cccdsInDb;
    private final Map<String, Nganh> nganhCache;
    private final Map<String, Role> roleCache;
    private final Set<String> usernamesInFile = new HashSet<>();
    private final Set<String> maHocVienInFile = new HashSet<>();
    private static final int BATCH_COUNT = 100;

    private int rowIndex = 1;
    private int successCount = 0;

    public HocVienExcelListener(
            HocVienAdminRepository hocVienAdminRepository,
            UsersAdminRepository usersAdminRepository,
            Map<String, Nganh> nganhCache,
            RoleAdminRepository roleAdminRepository,
            UserRoleAdminRepository userRoleAdminRepository,
            PasswordEncoder passwordEncoder) {
        this.hocVienAdminRepository = hocVienAdminRepository;
        this.usersAdminRepository = usersAdminRepository;
        this.roleAdminRepository = roleAdminRepository;
        this.userRoleAdminRepository = userRoleAdminRepository;
        this.passwordEncoder = passwordEncoder;
        this.maHocVienInDb = new HashSet<>(hocVienAdminRepository.findAllMaHocVien().stream()
                .filter(m -> m != null && !m.trim().isEmpty())
                .map(m -> m.trim().toUpperCase())
                .collect(Collectors.toSet()));
        this.usernamesInDb = usersAdminRepository.findAllUserNames().stream()
                .filter(u -> u != null && !u.trim().isEmpty())
                .map(u -> u.trim().toUpperCase())
                .collect(Collectors.toSet());
        this.cccdsInDb = usersAdminRepository.findAllCccds().stream()
                .filter(c -> c != null && !c.trim().isEmpty())
                .collect(Collectors.toSet());
        this.nganhCache = nganhCache;
        this.roleCache = loadRolesByCode(roleAdminRepository);
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

    @Override
    public void invoke(HocVienExcelDTO data, AnalysisContext context) {
        rowIndex++;

        if (data == null || data.getMaHocVien() == null || data.getMaHocVien().trim().isEmpty()) {
            errors.add("Dòng " + rowIndex + ": Mã học viên không được để trống");
            return;
        }

        String maHocVien = data.getMaHocVien().trim().toUpperCase();

        if (maHocVien.length() > 10) {
            errors.add("Dòng " + rowIndex + ": Mã học viên tối đa 10 ký tự");
            return;
        }

        if (maHocVienInFile.contains(maHocVien)) {
            errors.add("Dòng " + rowIndex + ": Mã học viên '" + maHocVien + "' bị trùng lặp trong file Excel");
            return;
        }
        maHocVienInFile.add(maHocVien);

        if (maHocVienInDb.contains(maHocVien)) {
            errors.add("Dòng " + rowIndex + ": Mã học viên '" + maHocVien + "' đã tồn tại trong cơ sở dữ liệu");
            return;
        }

        String maNganh = data.getMaNganh();
        if (maNganh == null || maNganh.trim().isEmpty()) {
            errors.add("Dòng " + rowIndex + ": Mã ngành không được để trống");
            return;
        }
        Nganh nganh = nganhCache.get(maNganh.trim().toUpperCase());
        if (nganh == null) {
            errors.add("Dòng " + rowIndex + ": Không tìm thấy ngành với mã '" + maNganh + "'");
            return;
        }

        String cccd = null;
        if (data.getCccd() != null && !data.getCccd().trim().isEmpty()) {
            cccd = data.getCccd().trim();
            if (!cccd.matches("^\\d{12}$")) {
                errors.add("Dòng " + rowIndex + ": CCCD phải gồm đúng 12 chữ số");
                return;
            }
            if (cccdsInDb.contains(cccd)) {
                errors.add("Dòng " + rowIndex + ": CCCD '" + cccd + "' đã tồn tại trong cơ sở dữ liệu");
                return;
            }
        }

        String username = "User_" + maHocVien;
        if (usernamesInFile.contains(username)) {
            errors.add("Dòng " + rowIndex + ": Tên đăng nhập '" + username + "' bị trùng lặp trong file Excel");
            return;
        }
        usernamesInFile.add(username);

        if (usernamesInDb.contains(username)) {
            errors.add("Dòng " + rowIndex + ": Tên đăng nhập '" + username + "' đã tồn tại trong cơ sở dữ liệu");
            return;
        }

        LocalDateTime ngayNhapHoc = parseDateTimeFromString(data.getNgayNhapHoc());
        LocalDateTime ngayTotNghiep = parseDateTimeFromString(data.getNgayTotNghiep());

        if (ngayTotNghiep != null && ngayNhapHoc != null && ngayTotNghiep.isBefore(ngayNhapHoc)) {
            errors.add("Dòng " + rowIndex + ": Ngày tốt nghiệp không được trước ngày nhập học");
            return;
        }

        Role role = null;
        if (data.getMaRole() != null && !data.getMaRole().trim().isEmpty()) {
            String maRole = data.getMaRole().trim().toUpperCase();
            role = roleCache.get(maRole);
            if (role == null) {
                errors.add("Dòng " + rowIndex + ": Mã vai trò '" + data.getMaRole().trim()
                        + "' không tồn tại trong hệ thống");
                return;
            }
        }

        Users user = new Users();
        user.setUserName(username);
        user.setHoTen(data.getHoTen() != null && !data.getHoTen().trim().isEmpty()
                ? data.getHoTen().trim()
                : "HocVien " + maHocVien);
        user.setPassWord(passwordEncoder.encode("123"));
        user.setGioiTinh(parseGioiTinh(data.getGioiTinh()));
        user.setTrangThai(true);

        if (cccd != null) {
            user.setCccd(cccd);
        }
        if (data.getEmail() != null && !data.getEmail().trim().isEmpty()) {
            user.setEmail(data.getEmail().trim());
        }
        if (data.getDiaChi() != null && !data.getDiaChi().trim().isEmpty()) {
            user.setDiaChi(data.getDiaChi().trim());
        }
        if (data.getSoDienThoai() != null && !data.getSoDienThoai().trim().isEmpty()) {
            String sdt = data.getSoDienThoai().trim();
            if (sdt.startsWith("+84")) {
                user.setSoDienThoai("0" + sdt.substring(3));
            } else {
                user.setSoDienThoai(sdt);
            }
        }
        if (data.getNgaySinh() != null && !data.getNgaySinh().trim().isEmpty()) {
            user.setNgaySinh(parseDateTimeFromString(data.getNgaySinh()));
        }

        try {
            user = usersAdminRepository.save(user);
        } catch (Exception e) {
            errors.add("Dòng " + rowIndex + ": Lỗi khi tạo user: " + e.getMessage());
            return;
        }

        HocVien hocVien = new HocVien();
        hocVien.setMaHocVien(maHocVien);
        hocVien.setUsers(user);
        hocVien.setNganh(nganh);
        hocVien.setNgayNhapHoc(ngayNhapHoc);
        hocVien.setNgayTotNghiep(ngayTotNghiep);
        toSave.add(new PendingHocVien(hocVien, user, role));

        if (toSave.size() >= BATCH_COUNT) {
            saveBatch();
        }
    }

    private LocalDateTime parseDateTimeFromString(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            String normalized = dateStr.trim().replace("T", " ");
            return LocalDateTime.parse(normalized, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            try {
                return LocalDateTime.parse(dateStr.trim(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (DateTimeParseException e2) {
                errors.add("Dòng " + rowIndex + ": Định dạng ngày không hợp lệ '" + dateStr
                        + "', vui lòng dùng định dạng 'yyyy-MM-dd HH:mm:ss'");
                return null;
            }
        }
    }

    private GioiTinhEnum parseGioiTinh(String gioiTinh) {
        if (gioiTinh == null || gioiTinh.trim().isEmpty()) {
            return null;
        }
        String normalized = gioiTinh.trim().toUpperCase();
        if ("NAM".equals(normalized) || "MALE".equals(normalized) || "M".equals(normalized)) {
            return GioiTinhEnum.NAM;
        } else if ("NU".equals(normalized) || "FEMALE".equals(normalized) || "F".equals(normalized)) {
            return GioiTinhEnum.NU;
        }
        return null;
    }

    private void saveBatch() {
        if (!toSave.isEmpty()) {
            try {
                List<HocVien> hocViensToSave = toSave.stream()
                        .map(PendingHocVien::hocVien)
                        .toList();
                hocVienAdminRepository.saveAll(hocViensToSave);

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
                    userRoleAdminRepository.saveAll(userRoles);
                }

                successCount += toSave.size();
                toSave.forEach(n -> maHocVienInDb.add(n.hocVien().getMaHocVien()));
                toSave.forEach(n -> usernamesInDb.add(n.users().getUsername()));
                toSave.forEach(n -> {
                    if (n.users().getCccd() != null) cccdsInDb.add(n.users().getCccd());
                });
            } catch (Exception e) {
                errors.add("Lỗi khi lưu batch: " + e.getMessage());
            } finally {
                toSave.clear();
            }
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveBatch();
    }

    public ExcelImportResult getResult() {
        ExcelImportResult result = new ExcelImportResult();
        result.setTotalRows(rowIndex - 1);
        result.setSuccessCount(successCount);
        result.setErrorCount(errors.size());
        result.setErrors(new ArrayList<>(errors));

        StringBuilder msg = new StringBuilder();
        if (successCount > 0) {
            msg.append("Đã thêm ").append(successCount).append(" học viên thành công");
        }
        if (errors.size() > 0) {
            if (msg.length() > 0) msg.append(". ");
            msg.append(errors.size()).append(" dòng lỗi");
        }
        if (msg.length() == 0) {
            msg.append("Không có dòng nào được xử lý");
        }
        result.setMessage(msg.toString());

        return result;
    }

    private record PendingHocVien(HocVien hocVien, Users users, Role role) {
    }
}
