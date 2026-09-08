package com.service;

import com.api_format.APIResponse;
import com.dtos.requestDtos.UserLoginRequestDtos;
import com.dtos.responseDtos.TokenResponseDto;
import com.entity.Roles;
import com.entity.UserDetail;
import com.enums.HttpStatusCode;
import com.enums.Role;
import com.enums.Status;
import com.exceptions.BusinessExceptions;
import com.ips.ClientIps;
import com.jwt.JwtService;
import com.mapper.UserMappers;
import com.repository.RoleRepository;
import com.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenRevocationService revocationService;
    private final UserRepository userRepository;
    private final UserMappers mappers;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;
   // private final ClientIps clientIps;

    private static final String audience = "audience";

    private static final String REFRESH_COOKIE_NAME = "__Host-refreshToken";

    private static final Integer maxAttempts = 5;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<APIResponse<Object>> createUser(UserLoginRequestDtos requestDtos, Errors errors) {

        boolean exits = userRepository.existsByEmail(requestDtos.getEmail());
        if(exits){
            return new ResponseEntity<>(APIResponse.apiResponse(HttpStatusCode.CONFLICT.getCode(), Collections.emptyList(), Status.ERROR.getName(), "Invalid email or password"), HttpStatus.CONFLICT);
        }

        Roles defaultRoles = roleRepository.findByRoles(Role.USER.getName()).orElseThrow(() -> new IllegalStateException("Default role not found"));
        UserDetail userDetail = mappers.toUserDetail(requestDtos);
        userDetail.setHashedPassword(passwordEncoder.encode(requestDtos.getPassword()));
        userDetail.setRoles(Set.of(defaultRoles));
        userRepository.save(userDetail);
        var pair = jwtService.issueAccessAndRefreshToken(userDetail, audience);
        TokenResponseDto responseDto = new TokenResponseDto(pair.accessToken());
        return new ResponseEntity<>(APIResponse.apiResponse(HttpStatusCode.SUCCESS.getCode(), responseDto,Status.SUCCESS.getName(), "user created successful"),HttpStatus.OK);

    }

    @Override
    public ResponseEntity<APIResponse<Object>> loginUser(UserLoginRequestDtos requestDtos, Errors errors) {

    UserDetail userDetail = userRepository.findByEmail(requestDtos.getEmail())
            .orElseThrow(() -> new BusinessExceptions.AccountNotFoundException("Account does not exits"));

    if(Boolean.TRUE.equals(userDetail.isLocked())){
        return new ResponseEntity<>(APIResponse.apiResponse(HttpStatusCode.FORBIDDEN.getCode(), Collections.emptyList(), Status.ERROR.getName(), "Account is locked"), HttpStatus.LOCKED);
    }

    boolean matchedPassword = passwordEncoder.matches(requestDtos.getPassword(), userDetail.getHashedPassword());
    if(!matchedPassword){
        registerFailedAttempts(userDetail);
        return  new ResponseEntity<>(APIResponse.apiResponse(HttpStatusCode.UNAUTHORIZED.getCode(), Collections.emptyList() , Status.ERROR.getName(), "Wrong passowrd or email"),HttpStatus.UNAUTHORIZED);
    }

    try{
        userDetail.setLoginAttempts(0);
        userDetail.setLockedUntil(null);
        userDetail.setOtpSecrect(null);
        userDetail.setMfaEnabled(false);
        userRepository.save(userDetail);

        var pair = jwtService.issueAccessAndRefreshToken(userDetail, audience);
        TokenResponseDto token = new TokenResponseDto(pair.accessToken());
        return new ResponseEntity<>(APIResponse.apiResponse(HttpStatusCode.SUCCESS.getCode(), token, Status.SUCCESS.getName(), "Login Sucessfull"),HttpStatus.OK);
    } catch (Exception e) {
        log.error("Failed to login :{}",e.getMessage());
        return new ResponseEntity<>(APIResponse.apiResponse(HttpStatusCode.INTERNAL_SERVER_ERROR.getCode(), Collections.emptyList(), Status.ERROR.getName(), "Internal Server error"),HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

@Override
public ResponseEntity<APIResponse<Object>> generateAccessToken(String refreshCookie){
        if(Objects.isNull(refreshCookie)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Claims claims;
        try {
            claims = jwtService.parseAndValidateRefreshToken(refreshCookie,audience);
        } catch (JwtException e) {
            log.error("Failed to validate refresh token :{}", e.getMessage());
            return ResponseEntity.status(HttpStatusCode.UNAUTHORIZED.getCode()).build();
        }
        Long userId = claims.get("userId", Long.class);
        UserDetail userDetail = userRepository.getReferenceById(userId);
        String accessToken = jwtService.issueAccessToken(userDetail, audience);

        return ResponseEntity.ok(APIResponse.apiResponse(HttpStatusCode.SUCCESS.getCode(), accessToken , Status.SUCCESS.getName(), "access token through refresh token"));

}

    @Override
    public ResponseEntity<APIResponse<Object>> logoutUser(String refreshCookie,String authHeader, HttpServletResponse response) {
        String accessToken = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
        if(refreshCookie != null){
              Claims claims = jwtService.parseAndValidateRefreshToken(refreshCookie, audience);
              revocationService.revokeToken(claims.getExpiration().toInstant(), claims.getId());
              clearRefreshCookie(response);
        }

        if(accessToken != null){
            Claims claims = jwtService.parseAndValidateAccessToken(accessToken, audience);

            revocationService.revokeToken(claims.getExpiration().toInstant(), claims.getId());

        }
        return new ResponseEntity<>(APIResponse.apiResponse(HttpStatusCode.SUCCESS.getCode(), Collections.emptyList(), Status.SUCCESS.getName(), "Logout successfully"),HttpStatus.OK);
    }

    private void registerFailedAttempts(UserDetail userDetail){
            int attempts = userDetail.getLoginAttempts() + 1;
            if(attempts >= maxAttempts){
            userDetail.setLockedUntil(Instant.now().plus(Duration.ofMinutes(10)));
            }
            userRepository.save(userDetail);
         }

        private void clearRefreshCookie(HttpServletResponse response){
            ResponseCookie cookie = ResponseCookie.from(REFRESH_COOKIE_NAME,"")
                    .secure(true)
                    .sameSite("Strict")
                    .httpOnly(true)
                    .path("/")
                    .maxAge(0)
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        }
}