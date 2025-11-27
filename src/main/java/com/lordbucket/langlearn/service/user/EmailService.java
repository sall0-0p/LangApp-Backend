package com.lordbucket.langlearn.service.user;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
public class EmailService {
    private final SpringTemplateEngine templateEngine;
    private final JavaMailSenderImpl mailSender;
    private final String fromEmail;

    public EmailService(@Qualifier("templateEngine") SpringTemplateEngine templateEngine,
                        JavaMailSenderImpl mailSender,
                        @Value("${spring.mail.username}") String fromEmail) {
        this.templateEngine = templateEngine;
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
    }

    @Async
    public void sendVerificationEmail(String to, String username, String token) {
        try {
            Context context = new Context();
            context.setVariable("username", username);

            // Build the URL your user will click
            String confirmationUrl = "http://langapp.lordbucket.eu/api/auth/confirm?token=" + token;
            context.setVariable("confirmationUrl", confirmationUrl);

            // Process the template
            String htmlContent = templateEngine.process("verification-email", context);

            // Create the email message
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Confirm Your Email");
            helper.setText(htmlContent, true);
            helper.setFrom(this.fromEmail);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }
}
