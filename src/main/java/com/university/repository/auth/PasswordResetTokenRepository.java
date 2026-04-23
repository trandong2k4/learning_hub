// package com.university.repository.auth;

// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository;

// import com.university.entity.PasswordResetToken;
// import com.university.entity.Users;

// import java.util.Optional;

// @Repository
// public interface PasswordResetTokenRepository extends
//         JpaRepository<PasswordResetToken, Long> {

//     // Tìm kiếm token trong DB
//     Optional<PasswordResetToken> findByToken(String token);

//     // Xóa token sau khi người dùng đã đổi mật khẩu thành công
//     void deleteByToken(String token);

//     // (Tùy chọn) Tìm token dựa trên User
//     Optional<PasswordResetToken> findByUser(Users users);
// }
