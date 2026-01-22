package com.example.authapp.services;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtpEmail(String toEmail, String otp) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(
                    new InternetAddress(
                            "sonukanojiya707@gmail.com",
                            "Capri Nexo"
                    )
            );

            helper.setTo(toEmail);
            helper.setSubject("Your OTP Verification Code");

            helper.setText(buildOtpTemplate(otp), true);

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("FAILED_TO_SEND_EMAIL", e);
        }
    }

    private String buildOtpTemplate(String otp) {
        return """
            <div style="font-family:Arial,Helvetica,sans-serif;
                        max-width:520px;
                        margin:auto;
                        padding:20px;
                        border:1px solid #eaeaea;
                        border-radius:6px">

              <h2 style="color:#007bff;margin-bottom:10px">
                Capri Nexo Security
              </h2>

              <p>Hello,</p>

              <p>Your One-Time Password (OTP) is:</p>

              <div style="
                   font-size:28px;
                   font-weight:bold;
                   letter-spacing:4px;
                   margin:15px 0;
                   color:#333">
                %s
              </div>

              <p>
                This OTP is valid for <b>2 minutes</b>.
              </p>

              <p style="color:#666">
                If you did not request this verification,
                please ignore this email.
              </p>

              <hr style="border:none;border-top:1px solid #eee">

              <p style="font-size:12px;color:#999">
                © 2026 Capri_Nexo • Internal Authentication System
              </p>

            </div>
        """.formatted(otp);
    }
}
