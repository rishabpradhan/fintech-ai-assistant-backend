package com.jwt;

import com.entity.Roles;
import com.entity.UserDetail;
import com.enums.Permissions;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.service.TokenRevocationService;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtService {

    @Value("${ACCESS_TOKEN_TIME}")
    private String accessTokenTime;

    @Value("${REFRESH_TOKEN_TIME}")
    private String refreshTokenTime;

    @Value("${PRIVATE_KEY}")
    private String secretKey;

    private static final String TOKEN_ISSUER = "project";
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String REFRESH_TOKEN = "refreshToken";

    private final TokenRevocationService revocationService;

    public record TokenPair(String accessToken, String refreshToken){}

    public TokenPair issueAccessAndRefreshToken(UserDetail user, String audience){
        String accessToken = issueAccessToken(user, audience);
        String refreshToken = issueRefreshToken(user, audience);

        return new TokenPair(accessToken, refreshToken);
    }

    public String issueAccessToken(UserDetail user, String audience){
        Instant now = Instant.now();
        Map<String, Object> claims = new HashMap<>();

        List<String> roles = user.getRoles().stream()
                .map(Roles::getName)
                .toList();
        claims.put("roles", roles);
        claims.put("userId", user.getId());
//
//        Set<String> permissions = user.getRoles().stream()
//                .flatMap(flat -> flat.getPermissions().stream())
//                .collect(Collectors.toSet());

        Set<String> permissions = user.getRoles().stream()
                        .flatMap(flat -> flat.getPermissions().stream().map(Permissions::getName))
                                .collect(Collectors.toSet());

        claims.put("permissions", permissions);
        return Jwts.builder()
                .issuer(TOKEN_ISSUER)
                .subject(user.getEmail())
                .audience().add(audience).and()
                .claims(claims)
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(Long.parseLong(accessTokenTime))))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    public String issueRefreshToken(UserDetail userDetail, String audience){
        Instant now = Instant.now();

        return Jwts.builder()
                .issuer(TOKEN_ISSUER)
                .subject(userDetail.getEmail())
                .audience().add(audience).and()
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(Long.parseLong(refreshTokenTime))))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    public Jws<Claims> parseAndValidateToken(String token, String audience){
    Jws<Claims> jws = Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
            .requireIssuer(TOKEN_ISSUER)
            .requireAudience(audience)
            .build()
            .parseSignedClaims(token);

    String jwtId = jws.getPayload().getId();
if(Boolean.TRUE.equals(revocationService.isRevoked(jwtId))){
 throw new JwtException("Token is revoked");
}

return jws;
}

public Claims parseAndValidateRefreshToken(String token, String audience){
    Claims claims = parseAndValidateToken(token, audience).getPayload();
    if(!REFRESH_TOKEN.equals(claims.get("type"))){
        throw new JwtException("Excepted a refresh token but got type =" + claims.get("type"));
    }
    return claims;
}

public Claims parseAndValidateAccessToken(String token , String audience){
        Claims claims = parseAndValidateToken(token, audience).getPayload();
        if(!ACCESS_TOKEN.equals(claims.get("type"))){
            throw new JwtException("Excepted access token but got type =" + claims.get("type"));
        }

        return  claims;
}


}



