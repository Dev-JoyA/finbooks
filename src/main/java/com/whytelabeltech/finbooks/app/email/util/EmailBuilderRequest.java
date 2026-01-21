package com.whytelabeltech.finbooks.app.email.util;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class EmailBuilderRequest {
    private String to;
    private List<String> tos;
    private Map<String, Object> props = new HashMap<>();
}
