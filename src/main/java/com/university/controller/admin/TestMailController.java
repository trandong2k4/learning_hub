package com.university.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.university.service.admin.SendGridService;

@RestController
@RequestMapping("/api/admin/test-mail")
public class TestMailController {

        @Autowired
        private SendGridService sendGridService;

        @GetMapping("/send-mail")
        public String test(
                        @RequestParam(value = "email", required = false) String email) {

                // Nếu không truyền param, mặc định gửi về cho chính mình để kiểm tra
                String targetEmail = (email != null) ? email : "nguyendong20041@gmail.com";

                System.out.println("Đang yêu cầu gửi mail thử nghiệm đến: " + targetEmail);

                // Nội dung mail nên dài một chút để tránh bộ lọc Spam của Gmail
                String htmlContent = "<html><body>" +
                                "<h2>Thông báo từ hệ thống Learning Hub</h2>" +
                                "<p>Chào bạn, đây là email kiểm tra tính năng gửi thư tự động.</p>" +
                                "<p>Thời gian gửi: " + java.time.LocalDateTime.now().toLocalDate() + "</p>" +
                                "<hr>" +
                                "<p style='color: gray;'>Vui lòng không phản hồi email này.</p>" +
                                "</body></html>";

                boolean success = sendGridService.sendEmail(
                                targetEmail,
                                "[Learning Hub] Kiểm tra cấu hình SendGrid",
                                htmlContent);

                return success ? "Đã gửi yêu cầu thành công tới " + targetEmail
                                : "Gửi thất bại! Hãy kiểm tra console log.";
        }
}