package com.university.service.auth;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Value("${sendgrid.api-key}")
    private String apiKey;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.frontend.reset-url}")
    private String resetUrl;

    public void sendResetPasswordMail(String toEmail, String token) {
        String link = resetUrl + "?token=" + token;

        Email from = new Email(fromEmail);
        Email to = new Email(toEmail);
        String subject = "Reset your password";

        String html = """
                <p>You requested a password reset.</p>
                <p>Click the link below to set a new password:</p>
                <p><a href="%s">Reset Password</a></p>
                <p>This link will expire in 15 minutes.</p>
                <p>If you did not request this, please ignore this email.</p>
                """.formatted(link);

        Mail mail = new Mail(from, subject, to, new Content("text/html", html));

        try {
            SendGrid sendGrid = new SendGrid(apiKey);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);

            if (response.getStatusCode() >= 400) {
                throw new RuntimeException("Cannot send email: " + response.getBody());
            }
        } catch (Exception e) {
            throw new RuntimeException("SendGrid error", e);
        }
    }
}
