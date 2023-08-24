package com.sinandemir.todoapp.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sinandemir.todoapp.entities.RefreshToken;
import com.sinandemir.todoapp.entities.User;
import com.sinandemir.todoapp.exceptions.ResourceNotFoundException;
import com.sinandemir.todoapp.repositories.RefreshTokenRepository;
import com.sinandemir.todoapp.security.JwtTokenProvider;

@Service
public class RefreshTokenService {
    private RefreshTokenRepository refreshTokenRepos;
    private JwtTokenProvider tokenProvider;
    private UserService userService;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepos, JwtTokenProvider tokenProvider,
            UserService userService) {
        this.refreshTokenRepos = refreshTokenRepos;
        this.tokenProvider = tokenProvider;
        this.userService = userService;
    }

    public RefreshToken generateRefreshToken(Long userId) {
        Optional<User> user = userService.findById(userId);
        if (user.isPresent()) {
            RefreshToken refreshToken = new RefreshToken();
            String token = tokenProvider.generateRefreshToken(userId);
            refreshToken.setRefreshToken(token);
            refreshToken.setUser(user.get());
            RefreshToken savedRefreshToken = refreshTokenRepos.save(refreshToken);
            return savedRefreshToken;
        }
        return null;
    }

    public RefreshToken getRefreshTokenByUserId(Long userId) {
        Optional<RefreshToken> refreshToken = refreshTokenRepos.findByUserId(userId);
        if (refreshToken.isPresent()) {
            return refreshToken.get();
        }
        return null;
    }

    public void deleteRefreshTokenByRefreshToken(String refreshToken) {
        RefreshToken findRefreshToken = refreshTokenRepos.findByRefreshToken(refreshToken).orElseThrow(
            () -> new ResourceNotFoundException("refresh token not found with token -> " + refreshToken)
        );

        refreshTokenRepos.deleteById(findRefreshToken.getId());

    }
}
