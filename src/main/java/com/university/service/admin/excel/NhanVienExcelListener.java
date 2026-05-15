package com.university.service.admin.excel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.university.dto.request.admin.NhanVienExcelDTO;
import com.university.dto.response.admin.ExcelImportResult;
import com.university.entity.NhanVien;
import com.university.entity.Users;
import com.university.enums.GioiTinhEnum;
import com.university.repository.admin.NhanVienAdminRepository;
import com.university.repository.admin.UsersAdminRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

public class NhanVienExcelListener extends AnalysisEventListener<NhanVienExcelDTO> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final NhanVienAdminRepository nhanVienAdminRepository;
    private final UsersAdminRepository usersAdminRepository;
    private final PasswordEncoder passwordEncoder;

    private final List<NhanVien> toSave = new ArrayList<>();
    private final List<String> errors = new ArrayList<>();

    private final Set<String> maNhanVienInDb;
    private final Set<String> usernamesInDb;
    private final Set<String> cccdsInDb;
    private final Set<String> usernamesInFile = new HashSet<>();
    private final Set<String> maNhanVienInFile = new HashSet<>();
    private static final int BATCH_COUNT = 100;

    private int rowIndex = 1;
    private int successCount = 0;

    public NhanVienExcelListener(
            NhanVienAdminRepository nhanVienAdminRepository,
            UsersAdminRepository usersAdminRepository,
            PasswordEncoder passwordEncoder) {
        this.nhanVienAdminRepository = nhanVienAdminRepository;
        this.usersAdminRepository = usersAdminRepository;
        this.passwordEncoder = passwordEncoder;
        this.maNhanVienInDb = nhanVienAdminRepository.findAllMaNhanVien().stream()
                .filter(maNhanVien -> maNhanVien != null && !maNhanVien.trim().isEmpty())
                .map(maNhanVien -> maNhanVien.trim().toUpperCase())
                .collect(Collectors.toSet());
        this.usernamesInDb = usersAdminRepository.findAllUserNames().stream()
                .filter(username -> username != null && !username.trim().isEmpty())
                .map(username -> username.trim().toUpperCase())
                .collect(Collectors.toSet());
        this.cccdsInDb = usersAdminRepository.findAllCccds().stream()
                .filter(cccd -> cccd != null && !cccd.trim().isEmpty())
                .collect(Collectors.toSet());
    }

    @Override
    public void invoke(NhanVienExcelDTO data, AnalysisContext context) {
        rowIndex++;

        if (data == null || data.getMaNhanVien() == null ||
                data.getMaNhanVien().trim().isEmpty()) {
            errors.add("Dòng " + rowIndex + ": Mã nhân viên không được để trống");
            return;
        }

        String maNhanVien = data.getMaNhanVien().trim().toUpperCase();

        if (maNhanVien.length() > 10) {
            errors.add("Dòng " + rowIndex + ": Mã nhân viên tối đa 10 ký tự");
            return;
        }

        if (maNhanVienInFile.contains(maNhanVien)) {
            errors.add("Dòng " + rowIndex + ": Mã nhân viên '" + maNhanVien + "' bị trùng lặp trong file Excel");
            return;
        }
        maNhanVienInFile.add(maNhanVien);

        if (maNhanVienInDb.contains(maNhanVien)) {
            errors.add("Dòng " + rowIndex + ": Mã nhân viên '" + maNhanVien + "' đã tồn tại trong cơ sở dữ liệu");
            return;
        }

        LocalDateTime ngayNhanViec = parseDateTime(data.getNgayNhanViec());
        LocalDateTime ngayNghiViec = parseDateTime(data.getNgayNghiViec());

        if (ngayNghiViec != null && ngayNhanViec != null && ngayNghiViec.isBefore(ngayNhanViec)) {
            errors.add("Dòng " + rowIndex + ": Ngày nghỉ việc không được trước ngày nhận việc");
            return;
        }

        if (data.getUsername() == null || data.getUsername().trim().isEmpty()) {
            errors.add("Dòng " + rowIndex + ": Tên đăng nhập không được để trống");
            return;
        }

        String username = data.getUsername().trim();
        if (usernamesInFile.contains(username)) {
            errors.add("Dòng " + rowIndex + ": Tên đăng nhập '" + username + "' bị trùng lặp trong file Excel");
            return;
        }
        usernamesInFile.add(username);

        if (usernamesInDb.contains(username)) {
            errors.add("Dòng " + rowIndex + ": Tên đăng nhập '" + username + "' đã tồn tại trong cơ sở dữ liệu");
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

        Users users = new Users();
        users.setUserName(username);
        users.setHoTen(maNhanVien);
        users.setPassWord(passwordEncoder.encode("123"));
        users.setTrangThai(true);
        users.setCccd(cccd);

        if (data.getEmail() != null && !data.getEmail().trim().isEmpty()) {
            users.setEmail(data.getEmail().trim());
        }
        if (data.getDiaChi() != null && !data.getDiaChi().trim().isEmpty()) {
            users.setDiaChi(data.getDiaChi().trim());
        }
        if (data.getSoDienThoai() != null && !data.getSoDienThoai().trim().isEmpty()) {
            String sdt = data.getSoDienThoai().trim();
            if (sdt.startsWith("+84")) {
                users.setSoDienThoai("0" + sdt.substring(3));
            } else {
                users.setSoDienThoai(sdt);
            }
        }
        if (data.getNgaySinh() != null && !data.getNgaySinh().trim().isEmpty()) {
            users.setNgaySinh(parseDateTime(data.getNgaySinh()));
        }
        if (data.getGioiTinh() != null && !data.getGioiTinh().trim().isEmpty()) {
            users.setGioiTinh(parseGioiTinh(data.getGioiTinh()));
        }

        try {
            users = usersAdminRepository.save(users);
        } catch (Exception e) {
            errors.add("Dòng " + rowIndex + ": Lỗi khi tạo tài khoản: " + e.getMessage());
            return;
        }

        NhanVien nhanVien = new NhanVien();
        nhanVien.setMaNhanVien(maNhanVien);
        nhanVien.setNgayNhanViec(ngayNhanViec);
        nhanVien.setNgayNghiViec(ngayNghiViec);
        nhanVien.setUsers(users);

        toSave.add(nhanVien);

        if (toSave.size() >= BATCH_COUNT) {
            saveBatch();
        }
    }

    private LocalDateTime parseDateTime(String dateStr) {
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
                nhanVienAdminRepository.saveAll(toSave);

                successCount += toSave.size();

                toSave.forEach(n -> maNhanVienInDb.add(n.getMaNhanVien()));
                toSave.forEach(n -> usernamesInDb.add(n.getUsers().getUsername()));
                toSave.forEach(n -> {
                    if (n.getUsers().getCccd() != null) cccdsInDb.add(n.getUsers().getCccd());
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
            msg.append("Đã thêm ").append(successCount).append(" nhân viên và ").append(successCount)
                    .append(" tài khoản thành công");
        }
        if (errors.size() > 0) {
            if (msg.length() > 0)
                msg.append(". ");
            msg.append(errors.size()).append(" dòng lỗi");
        }
        if (msg.length() == 0) {
            msg.append("Không có dòng nào được xử lý");
        }
        result.setMessage(msg.toString());

        return result;
    }
}
