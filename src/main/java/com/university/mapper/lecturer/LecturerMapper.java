package com.university.mapper.lecturer;

import com.university.dto.response.lecturer.AttendanceStudentResponseDTO;
import com.university.dto.response.lecturer.ComponentGradeEntryDTO;
import com.university.dto.response.lecturer.GradeStudentResponseDTO;
import com.university.dto.response.lecturer.LecturerClassStudentResponseDTO;
import com.university.dto.response.lecturer.LecturerProfileResponseDTO;
import com.university.dto.response.lecturer.LecturerScheduleDTO;
import com.university.entity.HocVien;
import com.university.entity.Lich;
import com.university.entity.NhanVien;
import com.university.entity.Users;
import com.university.entity.FileStorage;
import com.university.enums.FileEnum;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class LecturerMapper {

    public LecturerProfileResponseDTO toProfileResponse(Users user, NhanVien nhanVien,
            List<LecturerScheduleDTO> schedule) {
        return new LecturerProfileResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getHoTen(),
                user.getDiaChi(),
                user.getSoDienThoai(),
                user.getEmail(),
                user.getGioiTinh(),
                user.getNgaySinh(),
                user.getCccd(),
                nhanVien != null ? nhanVien.getMaNhanVien() : null,
                nhanVien != null ? nhanVien.getNgayNhanViec() : null,
                extractAvatarUrl(user),
                schedule);
    }

    public LecturerScheduleDTO toScheduleDTO(Lich lich) {
        return new LecturerScheduleDTO(
                lich.getId(),
                lich.getLopHocPhan().getId(),
                lich.getLopHocPhan().getMaLopHocPhan(),
                lich.getLopHocPhan().getMonHoc().getTenMonHoc(),
                lich.getNgayHoc(),
                lich.getGioHoc().getThoiGianBatDau().toString(),
                lich.getGioHoc().getThoiGianKetThuc().toString(),
                lich.getPhong().getTenPhong(),
                lich.getPhong().getToaNha());
    }

    public LecturerClassStudentResponseDTO toStudentDTO(HocVien hocVien) {
        return new LecturerClassStudentResponseDTO(
                hocVien.getId(),
                hocVien.getUsers().getHoTen(),
                hocVien.getMaHocVien(),
                extractAvatarUrl(hocVien.getUsers()));
    }

    public AttendanceStudentResponseDTO toAttendanceStudentDTO(HocVien hocVien, Users users,
            String trangThai) {

        AttendanceStudentResponseDTO attendanceStudentResponseDTO = new AttendanceStudentResponseDTO();
        attendanceStudentResponseDTO.setHocVienId(hocVien.getId());
        attendanceStudentResponseDTO.setHoTen(users.getHoTen());
        attendanceStudentResponseDTO.setMaHocVien(hocVien.getMaHocVien());
        attendanceStudentResponseDTO.setTrangThai(trangThai);
        attendanceStudentResponseDTO.setGhiChu(null);
        attendanceStudentResponseDTO.setSoBuoiVang(0);
        return attendanceStudentResponseDTO;
    }

    public GradeStudentResponseDTO toGradeStudentDTO(HocVien hocVien, Float average,
            List<ComponentGradeEntryDTO> diemThanhPhan) {
        return new GradeStudentResponseDTO(
                hocVien.getId(),
                hocVien.getUsers().getHoTen(),
                hocVien.getMaHocVien(),
                average,
                diemThanhPhan);
    }

    public String extractAvatarUrl(Users user) {
        if (user == null || user.getDFileStorages() == null) {
            return null;
        }
        Optional<FileStorage> avatar = user.getDFileStorages().stream()
                .filter(fs -> fs.getFileType() == FileEnum.AVATAR)
                .min(Comparator.comparing(FileStorage::getCreatedAt));
        return avatar.map(FileStorage::getFileUrl).orElse(null);
    }
}
