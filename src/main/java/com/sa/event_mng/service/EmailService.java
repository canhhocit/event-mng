package com.sa.event_mng.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String token) {
        String subject = "Email Verification";
        String verificationUrl = "http://localhost:8080/event-mng/auth/verify?token=" + token;
        String message = "Please click the link below to verify your email:\n" + verificationUrl;

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(to);
        email.setSubject(subject);
        email.setText(message);

        mailSender.send(email);
    }

    public void sendOtpEmail(String to, String otp) {
        String subject = "Mã xác thực quên mật khẩu";
        String message = "Mã OTP để đặt lại mật khẩu của bạn là: " + otp + "\n" +
                         "Nếu bạn không yêu cầu, vui lòng bỏ qua email này.";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(to);
        email.setSubject(subject);
        email.setText(message);

        mailSender.send(email);
    }
}
