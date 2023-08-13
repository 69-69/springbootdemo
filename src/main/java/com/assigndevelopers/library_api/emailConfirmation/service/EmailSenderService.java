package com.assigndevelopers.library_api.emailConfirmation.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {

    private final JavaMailSender javaMailSender;

    @Value("${assigndevelopers.app.hostName}")
    private String hostName;

    public EmailSenderService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    protected void sendMessage(SimpleMailMessage email) {
        javaMailSender.send(email);
    }

    public void sendMail(String token, String userEmail) {
        final SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setTo(userEmail);
        mailMessage.setSubject("Mail Confirmation Link!");
        mailMessage.setFrom("<MAIL>");
        mailMessage.setText(
                "Thank you for registering. Link expires after 10 minutes." +
                        "\n Please click on the below link to activate your account.\n "
                        + hostName + "/register/confirm_email?token="
                        + token);

        sendMessage(mailMessage);
    }
}
