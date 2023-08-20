package com.sinandemir.todoapp.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sinandemir.todoapp.dto.requests.RefreshTokenRequest;
import com.sinandemir.todoapp.dto.requests.UserLoginRequest;
import com.sinandemir.todoapp.dto.requests.UserRegisterRequest;
import com.sinandemir.todoapp.dto.responses.UserLoginResponse;
import com.sinandemir.todoapp.dto.responses.UserRegisterResponse;
import com.sinandemir.todoapp.services.AuthService;
import com.sinandemir.todoapp.services.RefreshTokenService;

import io.jsonwebtoken.ExpiredJwtException;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    private AuthService authService;
    private RefreshTokenService refreshTokenService;

    public AuthController(AuthService authService, RefreshTokenService refreshTokenService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("register")
    public ResponseEntity<UserRegisterResponse> register(@RequestBody UserRegisterRequest registerRequest) {
        UserRegisterResponse user = authService.register(registerRequest);
        return new ResponseEntity<UserRegisterResponse>(user, HttpStatus.CREATED);
    }

    @PostMapping("login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginRequest loginRequest) {
        UserLoginResponse user = authService.login(loginRequest);
        return new ResponseEntity<UserLoginResponse>(user, HttpStatus.OK);
    }

    @PostMapping("refresh-token")
    public ResponseEntity<String> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        try {
            String newJwtToken = authService.refreshAccessToken(refreshTokenRequest.getRefreshToken());
            if (newJwtToken != null) {
                return new ResponseEntity<String>(newJwtToken, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<String>("Token refresh failed.", HttpStatus.BAD_REQUEST);
            }
        } catch (ExpiredJwtException e) {
            refreshTokenService.deleteRefreshTokenByRefreshToken(refreshTokenRequest.getRefreshToken());
            return new ResponseEntity<String>("Session has expired.", HttpStatus.UNAUTHORIZED);
        }
    }
}
