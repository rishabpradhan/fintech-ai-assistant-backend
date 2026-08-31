package com.service;

import com.api_format.APIResponse;
import com.dtos.requestDtos.UserLoginRequestDtos;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;

public interface UserService {

     ResponseEntity<APIResponse<Object>> createUser(UserLoginRequestDtos requestDtos, Errors errors);

     ResponseEntity<APIResponse<Object>> loginUser(UserLoginRequestDtos requestDtos, Errors errors);

     ResponseEntity<APIResponse<Object>> generateAccessToken(String refreshCookie);

     ResponseEntity<APIResponse<Object>> logoutUser(String refreshCookie, String authHeader, HttpServletResponse response);
}
