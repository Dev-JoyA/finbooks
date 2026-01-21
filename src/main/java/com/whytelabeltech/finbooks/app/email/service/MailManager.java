package com.whytelabeltech.finbooks.app.email.service;


import com.whytelabeltech.finbooks.app.email.util.EmailBuilderRequest;

public interface MailManager {
    void sendSuccessfulRegistrationEmail (EmailBuilderRequest emailBuilderRequest);

    void sendOtpEmailToResetPassword(EmailBuilderRequest emailBuilderRequest);

}
