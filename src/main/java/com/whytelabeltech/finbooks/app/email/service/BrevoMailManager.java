package com.whytelabeltech.finbooks.app.email.service;

import com.whytelabeltech.finbooks.app.email.util.EmailBuilderRequest;
import com.whytelabeltech.finbooks.app.email.util.MailBuilder;
import com.whytelabeltech.finbooks.middleware.exception.error.FinbookException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Arrays;
import java.util.Map;

@Slf4j
@Service(value = "brevo")
public class BrevoMailManager implements MailManager{

    @Value("${BREVO_API_KEY}")
    private String brevoApiKey;

    @Autowired
    private RestTemplate restTemplate;


    @Autowired
    private TemplateEngine templateEngine;

    @Value("${sender.email.address}")
    private String senderEmailAddress;


    private void sendMail(MailBuilder mailBuilder) {

        String htmlContent = createHtmlContext(mailBuilder);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", brevoApiKey);

        Map<String, Object> payload = Map.of(
                "sender", Map.of(
                        "email", senderEmailAddress,
                        "name", "WHYTLABEL TECH"
                ),
                "to", Arrays.stream(
                        mailBuilder.getTos() != null
                                ? mailBuilder.getTos()
                                : new String[]{ mailBuilder.getTo() }
                ).map(email -> Map.of("email", email)).toList(),
                "subject", mailBuilder.getSubject(),
                "htmlContent", htmlContent
        );

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(payload, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://api.brevo.com/v3/smtp/email",
                entity,
                String.class
        );

        log.info("📩 Brevo response code: {}", response.getStatusCode());

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new FinbookException("Brevo email failed: " + response.getBody());
        }
    }


    private String createHtmlContext(MailBuilder mailBuilder) {
        Context context = new Context();
        context.setVariables(mailBuilder.getProps());
        return templateEngine.process(mailBuilder.getHtml(), context);
    }

    @Override
    @Async
    public void sendSuccessfulRegistrationEmail(EmailBuilderRequest emailBuilderRequest){
        System.out.println("📧 Attempting to send registration email to: " + emailBuilderRequest.getTo());
        MailBuilder mailBuilder = new MailBuilder();
        mailBuilder.setTo(emailBuilderRequest.getTo());
        mailBuilder.setSubject("Registration Successful");
        mailBuilder.setHtml("registration-email-template");
        mailBuilder.setProps(emailBuilderRequest.getProps());

        try {
            sendMail(mailBuilder);
            System.out.println("Registration email sent to: " + emailBuilderRequest.getTo());
        } catch (Exception e) {
            System.out.println("Error while sending mail to: " + emailBuilderRequest.getTo());
            log.error("Brevo email error", e);
            throw new FinbookException("error occurred while sending mail");
        }

    }

    @Override
    @Async
    public void sendOtpEmailToResetPassword(EmailBuilderRequest emailBuilderRequest) {
        MailBuilder mailBuilder = new MailBuilder();
        mailBuilder.setTo(emailBuilderRequest.getTo());
        mailBuilder.setSubject("Password Reset");
        mailBuilder.setHtml("otp-email-template");
        mailBuilder.setProps(emailBuilderRequest.getProps());

        try {
            sendMail(mailBuilder);
        } catch (Exception e) {
            log.error("Brevo email error", e);
            throw new FinbookException("error occurred while sending mail");
        }


    }

}

