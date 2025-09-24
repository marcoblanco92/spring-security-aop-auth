package com.marbl.spring_security_aop_auth.component.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class CorrelationIdFilter implements Filter {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String CORRELATION_ID_KEY = "correlationId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        try {
            HttpServletRequest httpRequest = (HttpServletRequest) request;

            // Check if the header is already present
            String correlationId = httpRequest.getHeader(CORRELATION_ID_HEADER);

            // If missing, generate a new one
            if (correlationId == null || correlationId.isBlank()) {
                correlationId = UUID.randomUUID().toString();
            }

            // Put correlationId into MDC so it will be included in all logs
            MDC.put(CORRELATION_ID_KEY, correlationId);

            chain.doFilter(request, response);
        } finally {
            // Always clear MDC after request to prevent leaking values to other threads
            MDC.remove(CORRELATION_ID_KEY);
        }
    }
}