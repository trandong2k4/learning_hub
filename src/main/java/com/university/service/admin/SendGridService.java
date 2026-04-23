package com.university.service.admin;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
public class SendGridService {

    @Value("${sendgrid.api.key}")
    private String apiKey;

    @Value("${app.mail.from}")
    private String fromEmail;

    public boolean sendEmail(String toAddr, String subject, String bodyContent) {
        // 1. Kiểm tra log để chắc chắn giá trị fromEmail đã đúng
        System.out.println("--- Khởi tạo tiến trình gửi mail ---");
        System.out.println("Gửi từ (Sender): " + fromEmail);
        System.out.println("Gửi đến (Receiver): " + toAddr);

        // 2. Thiết lập đối tượng Mail
        Email from = new Email(fromEmail);
        Email to = new Email(toAddr);
        Content content = new Content("text/html", bodyContent);
        Mail mail = new Mail(from, subject, to, content);

        // 3. Gọi API SendGrid
        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            // In log kết quả
            System.out.println("--- SendGrid Response ---");
            System.out.println("Status Code: " + response.getStatusCode());
            if (response.getBody() != null && !response.getBody().isEmpty()) {
                System.out.println("Response Body: " + response.getBody());
            }
            System.out.println("-------------------------");

            // Mã 202 là thành công
            return response.getStatusCode() >= 200 && response.getStatusCode() < 300;

        } catch (IOException ex) {
            System.err.println("Lỗi kết nối hệ thống SendGrid: " + ex.getMessage());
            return false;
        }
    }
}