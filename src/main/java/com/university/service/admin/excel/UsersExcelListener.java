package com.university.service.admin.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.university.dto.request.admin.UsersAdminRequestDTO;
import com.university.dto.response.admin.ExcelImportResult;
import com.university.entity.Users;
import com.university.repository.admin.UsersAdminRepository;

import org.springframework.beans.BeanUtils;

import java.util.*;

public class UsersExcelListener extends
        AnalysisEventListener<UsersAdminRequestDTO> {

    private final UsersAdminRepository usersRepository;

    private final List<Users> toSave = new ArrayList<>();
    private final List<String> errors = new ArrayList<>();

    private final Set<String> maUsersInFile = new HashSet<>(); // Kiểm tra trùng trong file
    private final Set<String> maUsernameInDb;

    private static final int BATCH_COUNT = 50; // Tăng để hiệu suất tốt

    private int rowIndex = 1;
    private int successCount = 0;

    public UsersExcelListener(UsersAdminRepository usersRepository) {
        this.usersRepository = usersRepository;
        this.maUsernameInDb = new HashSet<>(usersRepository.findAllUserNames());
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

        // Chuyển sang Entity
        Users users = new Users();
        BeanUtils.copyProperties(data, users);
        toSave.add(users);

        // Lưu batch
        if (toSave.size() >= BATCH_COUNT) {
            saveBatch();
        }
    }

    private void saveBatch() {
        if (!toSave.isEmpty()) {
            try {
                usersRepository.saveAll(toSave);

                // ✔ chỉ tăng khi save OK
                successCount += toSave.size();

                // ✔ update DB cache (tránh duplicate batch sau)
                toSave.forEach(n -> maUsernameInDb.add(n.getUsername()));

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
}