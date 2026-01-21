package com.whytelabeltech.finbooks.app.email.util;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class MailBuilder {
    private String to;
    private String[] tos;
    private String subject;
    private String html;
    private Map<String, Object> props;
}