// package com.university.service.student;

// import java.util.UUID;

// import org.springframework.stereotype.Service;

// import com.university.dto.request.student.HocVienProfileRequestDTO;
// import com.university.dto.response.student.HocVienProfileResponseDTO;
// import com.university.entity.Users;
// import com.university.repository.UserRepository;
// import com.university.repository.student.HocVienProfileRepository;

// @Service
// public class HocVienProfileService {

// private final HocVienProfileRepository hocVienRepo;
// private final UserRepository userRepo;

// public HocVienProfileService(HocVienProfileRepository hocVienRepo,
// UserRepository userRepo) {
// this.hocVienRepo = hocVienRepo;
// this.userRepo = userRepo;
// }

// // GET PROFILE
// public HocVienProfileResponseDTO getProfile(UUID userId) {
// return hocVienRepo.findHocVienProfileByUserId(userId)
// .orElseThrow(() -> new RuntimeException("Không tìm thấy học viên"));
// }

// // UPDATE PROFILE
// public HocVienProfileResponseDTO updateProfile(UUID userId,
// HocVienProfileRequestDTO req) {

// Users user = userRepo.findById(userId)
// .orElseThrow(() -> new RuntimeException("User không tồn tại"));

// hocVienRepo.findByUsers_Id(userId)
// .orElseThrow(() -> new RuntimeException("Không tìm thấy học viên"));

// // update user
// user.setHoTen(req.getHoTen());
// user.setDiaChi(req.getDiaChi());
// user.setSoDienThoai(req.getSoDienThoai());
// user.setEmail(req.getEmail());
// user.setGioiTinh(req.getGioiTinh());
// user.setNgaySinh(req.getNgaySinh());
// user.setCccd(req.getCccd());

// userRepo.save(user);
// // trả lại profile mới
// return getProfile(userId);
// }
// }