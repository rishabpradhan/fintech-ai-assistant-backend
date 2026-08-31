package com.ips;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@Component
public class ClientIps {

    public String getClientIp(){
        ServletRequestAttributes attrs =(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if(Objects.isNull(attrs)){
            return "unknown ips address";
        }

        HttpServletRequest request = attrs.getRequest();
        // if client is using ngnix or load balancer this code to is to get actual client id

        String forwardedFor = request.getHeader("X-Forwarded-For");
        if(StringUtils.hasText(forwardedFor)){
            return forwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }
}

