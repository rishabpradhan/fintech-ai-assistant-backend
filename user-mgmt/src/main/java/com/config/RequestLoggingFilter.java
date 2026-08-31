package com.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final String correlationHeader = "X-CORRELATION-Id";


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String correlationId = request.getHeader(correlationHeader);
        if(correlationId == null || correlationId.isBlank()){
            correlationId = UUID.randomUUID().toString();
        }
        response.setHeader(correlationHeader,correlationId);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String user = (!Objects.isNull(auth) && auth.isAuthenticated()) ? auth.getName() : "anonymous user";

        long time = System.currentTimeMillis();
        filterChain.doFilter(request,response);
        try{
            log.info("[{}] {} {} - user={}",correlationId,request.getMethod(), request.getRequestURI(), user);
        } finally {
            log.info("[{}] {} {} - status={} durationMs={}", correlationId, request.getMethod(),
                    request.getRequestURI(), response.getStatus(), System.currentTimeMillis() - time);
        }
    }


  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException{
        String path = request.getRequestURI();
        return path.startsWith("/acutator");
  }

}

