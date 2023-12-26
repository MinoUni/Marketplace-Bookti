package com.teamchallenge.bookti.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {

    private final String[] permitAllReq;
    private final List<String> allowedOrigins;

    public ApplicationProperties(List<String> permitAllReq, List<String> allowedOrigins) {
        this.permitAllReq = permitAllReq.toArray(String[]::new);
        this.allowedOrigins = allowedOrigins;
    }
}
