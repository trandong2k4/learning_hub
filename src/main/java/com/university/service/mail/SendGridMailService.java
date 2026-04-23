package com.university.service.mail;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SendGridMailService {

    @Value("${sendgrid.api.key}")
    private String apiKey;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.frontend.reset-password-url}")
    private String resetPasswordUrl;

    public void sendResetPasswordEmail(String toEmail, String token) {
        String resetLink = resetPasswordUrl + "?token=" + token;

        Email from = new Email(fromEmail);
        Email to = new Email(toEmail);
        String subject = "Khôi phục mật khẩu";

        String htmlContent = """
                <p>Bạn đã yêu cầu khôi phục mật khẩu cho tài khoản của mình.</p>
                <p>Nhấn vào liên kết bên dưới để đặt lại mật khẩu:</p>
                <p><a href="%s">Đặt lại mật khẩu</a></p>
                <p>Liên kết này có hiệu lực trong 15 phút.</p>
                <p>Nếu bạn không yêu cầu thao tác này, hãy bỏ qua email.</p>
                """.formatted(resetLink);

        Mail mail = new Mail(from, subject, to, new Content("text/html", htmlContent));

        try {
            SendGrid sendGrid = new SendGrid(apiKey);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);

            if (response.getStatusCode() >= 400) {
                throw new RuntimeException("Gửi email thất bại: " + response.getBody());
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi gọi SendGrid API", e);
        }
    }
}
