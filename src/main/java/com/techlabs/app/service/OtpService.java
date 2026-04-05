package com.techlabs.app.service;

import com.techlabs.app.entity.Otp;
import com.techlabs.app.exception.FortuneLifeException;
import com.techlabs.app.repository.OtpRepository;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Service
public class OtpService {

    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);

    @Autowired
    private MailService mailService;

    @Autowired
    private OtpRepository otpRepository;

    private final int OTP_LENGTH = 6;

    public void sendOtp(String sourceType, String sourceValue) throws MessagingException {
        if (!sourceType.equalsIgnoreCase("email")) {
            throw new FortuneLifeException("Only email-based OTP is supported. SMS OTP is not available.");
        }

        String otpGenerated = generateOtp();
        LocalDateTime expirationTime = LocalDateTime.now().plus(10, ChronoUnit.MINUTES);

        Otp otp = new Otp();
        otp.setSource(sourceValue);
        otp.setOtpCode(otpGenerated);
        otp.setExpirationTime(expirationTime);
        otp.setCreatedTime(LocalDateTime.now());

        String messageBody = "Dear customer, your OTP for resetting your Login Password is: "
                + otpGenerated + ". Please use this code to proceed. This OTP is valid for 10 minutes.";
        String subject = "Your OTP for Password Reset Request";

        mailService.mailWithAttachment(sourceValue, subject, messageBody);
        otpRepository.save(otp);

        logger.info("OTP sent via email to: {}", sourceValue);
    }

    public String generateOtp() {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }
}
