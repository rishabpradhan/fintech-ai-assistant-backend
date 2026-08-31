package com.controller;

import com.api_format.APIResponse;
import com.dtos.requestDtos.UserLoginRequestDtos;
import com.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private static final String REFRESH_COOKIE_NAME = "__Host-refreshToken";


    @PostMapping("/signup")
    public ResponseEntity<APIResponse<Object>> createUser(@Valid @RequestBody UserLoginRequestDtos requestDtos, Errors errors){

        return userService.createUser(requestDtos, errors);

    }

    @PostMapping("/login")
    public ResponseEntity<APIResponse<Object>> login(@Valid @RequestBody UserLoginRequestDtos requestDtos, Errors errors){

        return userService.loginUser(requestDtos, errors);
    }

    @PostMapping("/accessToken")
    public ResponseEntity<APIResponse<Object>> generateAccessTokenThroughRefreshToken(@CookieValue(name = REFRESH_COOKIE_NAME, required = false) String refreshCookie){

        return userService.generateAccessToken(refreshCookie);
    }

    @PostMapping("/logout")
    public ResponseEntity<APIResponse<Object>> logout(@CookieValue(name = REFRESH_COOKIE_NAME, required = false) String refreshCookie,
                                                      @RequestHeader(value = "Authorization") String authHeader, HttpServletResponse response){
        return userService.logoutUser(refreshCookie,authHeader,response);
    }

}
