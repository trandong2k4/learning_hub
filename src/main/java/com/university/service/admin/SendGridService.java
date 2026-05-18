package com.university.service.admin;

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
public class SendGridService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.mail.from-name:He thong}")
    private String fromName;

    public boolean sendEmail(String toAddr, String subject, String bodyContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            setFrom(helper);
            helper.setTo(toAddr);
            helper.setSubject(subject);
            helper.setText(bodyContent, true);
            mailSender.send(message);
            return true;
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Lỗi gửi email tới {}: {}", toAddr, e.getMessage());
            return false;
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
