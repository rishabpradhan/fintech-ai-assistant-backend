package com.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class TokenRevocationService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String REVOKED_LIST = "revoked:";

    public void revokeToken(Instant tokenExpiry, String jwtId){
        Duration toLive = Duration.between(Instant.now(), tokenExpiry);
        if(toLive.isNegative() || toLive.isZero()){
            return;
        }
        redisTemplate.opsForValue().set(
                "revoked",
                REVOKED_LIST + jwtId,
                toLive
        );
    }
    public Boolean isRevoked(String jwtId){
        return Boolean.TRUE.equals(redisTemplate.hasKey(REVOKED_LIST + jwtId));
    }

}
