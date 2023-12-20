package com.teamchallenge.bookti.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final HandlerExceptionResolver resolver;

    public CustomAccessDeniedHandler(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) {
        log.error("Denied access with AccessDeniedException. Details: {}", accessDeniedException.getMessage());
        resolver.resolveException(request, response, null, accessDeniedException);
    }
}
