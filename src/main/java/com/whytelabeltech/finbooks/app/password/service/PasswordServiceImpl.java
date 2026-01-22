package com.whytelabeltech.finbooks.app.password.service;

import com.whytelabeltech.finbooks.app.email.service.MailManager;
import com.whytelabeltech.finbooks.app.email.util.EmailBuilderRequest;
import com.whytelabeltech.finbooks.app.password.dto.AttemptForgotPasswordDto;
import com.whytelabeltech.finbooks.app.password.dto.ForgotPasswordDto;
import com.whytelabeltech.finbooks.app.shared.dto.MessageResponse;
import com.whytelabeltech.finbooks.app.user.model.User;
import com.whytelabeltech.finbooks.app.user.repository.UserRepository;
import com.whytelabeltech.finbooks.middleware.exception.error.PasswordException;
import com.whytelabeltech.finbooks.middleware.exception.error.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class PasswordServiceImpl implements PasswordService{

    @Autowired
    UserRepository userRepository;

    @Autowired
    @Qualifier(value = "brevo")
    MailManager mailManager;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public MessageResponse forceResetPassword(AttemptForgotPasswordDto attemptForgotPasswordDto) {
        User user = userRepository.findByEmail(attemptForgotPasswordDto.getEmail()).orElseThrow(() ->
                new UserException("Invalid Email Address"));

        String otp = generateOtp();

        while (userRepository.existsByOtp(otp)) {
            otp = generateOtp();
        }
        user.setOtp(otp);
        userRepository.save(user);

        EmailBuilderRequest emailBuilderRequest = new EmailBuilderRequest();
        emailBuilderRequest.setTo(user.getEmail());
        emailBuilderRequest.getProps().put("name", user.getUsername());
        emailBuilderRequest.getProps().put("otp", otp);
        mailManager.sendOtpEmailToResetPassword(emailBuilderRequest);

        return new MessageResponse("Otp has been sent to your email");

    }

    @Override
    public MessageResponse validateOtp(String otp) {
        if(!userRepository.existsByOtp(otp)) throw new PasswordException("Invalid OTP");

        return new MessageResponse("Otp validated successfully");
    }

    @Override
    public MessageResponse forgotPassword(ForgotPasswordDto forgotPasswordDto) {
        if (userRepository.existsByOtp(forgotPasswordDto.getOtp())){
            User user = userRepository.findByEmail(forgotPasswordDto.getEmail())
                    .orElseThrow(() -> new UserException("invalid email"));
            if (forgotPasswordDto.getNewPassword().equals(forgotPasswordDto.getConfirmPassword())){
                user.setPassword(passwordEncoder.encode(forgotPasswordDto.getConfirmPassword()));
                userRepository.save(user);

                return new MessageResponse("Password reset successfully");
            }else {
                throw new PasswordException("new password and confirm password not matched");
            }

        }else {
            throw new PasswordException("invalid otp");
        }

    }

    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        int number = 9 * random.nextInt(100000);
        return java.lang.String.format("%06d", number);
    }
}
