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
public class EmailService {

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Value("${learninghub.email.from}")
    private String emailFrom;

    public void sendTestEmail(String toEmail) {
        Email from = new Email(emailFrom);
        String subject = "Chào mừng bạn đến với Learning Hub!";
        Email to = new Email(toEmail);
        Content content = new Content("text/plain", "Đây là email gửi thử nghiệm từ hệ thống sử dụng SendGrid API.");

        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);

            System.out.println("Status Code: " + response.getStatusCode());
            System.out.println("Body: " + response.getBody());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
