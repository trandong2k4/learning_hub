package com.university.service.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
@Slf4j
public class SendGridMailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.mail.from-name:He thong}")
    private String fromName;

    @Value("${app.frontend.reset-password-url:}")
    private String resetPasswordUrl;

    @Value("${app.frontend.invoice-url:}")
    private String invoiceUrl;

    public void sendResetPasswordEmail(String toEmail, String token) {
        String resetLink = resetPasswordUrl + "?token=" + token;
        String html = """
                <p>Bạn đã yêu cầu khôi phục mật khẩu cho tài khoản của mình.</p>
                <p>Nhấn vào liên kết bên dưới để đặt lại mật khẩu:</p>
                <p><a href="%s">Đặt lại mật khẩu</a></p>
                <p>Liên kết này có hiệu lực trong 15 phút.</p>
                <p>Nếu bạn không yêu cầu thao tác này, hãy bỏ qua email.</p>
                """.formatted(resetLink);
        sendHtml(toEmail, "Khôi phục mật khẩu", html);
    }

    public void sendTuitionNotificationEmail(String toEmail, String token, String studentName, Double amount,
            String hocPhiId) {
        String invoiceLink = invoiceUrl != null && !invoiceUrl.isBlank()
                ? invoiceUrl + "?token=" + token
                : "";
        String html = """
                <p>Chào %s,</p>
                <p>Hệ thống Learning Hub gửi thông báo học phí cho bạn.</p>
                <p><strong>Số tiền:</strong> %,.0f VND</p>
                <p><strong>Mã học phí:</strong> %s</p>
                %s
                <p>Nếu bạn đã thanh toán, vui lòng bỏ qua email này.</p>
                """.formatted(
                studentName == null ? "" : studentName,
                amount == null ? 0.0 : amount,
                hocPhiId == null ? "" : hocPhiId,
                invoiceLink.isBlank() ? "" : "<p><a href=\"" + invoiceLink + "\">Xem chi tiết &amp; thanh toán</a></p>");
        sendHtml(toEmail, "Thông báo học phí", html);
    }

    private void sendHtml(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            setFrom(helper);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Lỗi khi gửi email", e);
        }
    }

    private void setFrom(MimeMessageHelper helper) throws MessagingException, UnsupportedEncodingException {
        if (fromName == null || fromName.isBlank()) {
            helper.setFrom(fromEmail);
            return;
        }
        helper.setFrom(fromEmail, fromName);
    }
}
