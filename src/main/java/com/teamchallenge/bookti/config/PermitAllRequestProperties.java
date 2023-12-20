package com.teamchallenge.bookti.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@ConfigurationProperties(prefix = "application")
public class PermitAllRequestProperties {

    private final String[] permitAllReq;

    public PermitAllRequestProperties(List<String> permitAllReq) {
        this.permitAllReq = permitAllReq.toArray(String[]::new);
    }
}
