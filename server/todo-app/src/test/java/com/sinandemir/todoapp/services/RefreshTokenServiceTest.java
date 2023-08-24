package com.sinandemir.todoapp.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import com.sinandemir.todoapp.entities.RefreshToken;
import com.sinandemir.todoapp.entities.User;
import com.sinandemir.todoapp.exceptions.ResourceNotFoundException;
import com.sinandemir.todoapp.repositories.RefreshTokenRepository;
import com.sinandemir.todoapp.security.JwtTokenProvider;

@SpringBootTest
public class RefreshTokenServiceTest {

    @InjectMocks
    private RefreshTokenService cut;

    @Mock
    private RefreshTokenRepository refreshTokenRepos;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test getRefreshTokenByUserId")
    void should_get_refresh_token_by_user_id() {
        Long userId = 1L;
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(1L);

        when(refreshTokenRepos.findByUserId(userId)).thenReturn(Optional.of(refreshToken));

        RefreshToken result = cut.getRefreshTokenByUserId(userId);

        assertNotNull(result);
        assertEquals(refreshToken.getId(), result.getId());

        verify(refreshTokenRepos).findByUserId(userId);
    }

    @Test
    @DisplayName("Test getRefreshTokenByUserId should return null")
    void get_refresh_token_by_user_id_should_return_null() {
        Long userId = 1L;

        when(refreshTokenRepos.findByUserId(userId)).thenReturn(Optional.empty());

        RefreshToken result = cut.getRefreshTokenByUserId(userId);

        assertNull(result);
        verify(refreshTokenRepos).findByUserId(userId);
    }

    @Test
    @DisplayName("Test deleteRefreshTokenByRefreshToken")
    void should_delete_refresh_token_by_refresh_token() {
        String fakeRefreshToken = "fake-refresh-token";

        RefreshToken token = new RefreshToken();
        token.setId(1L);

        when(refreshTokenRepos.findByRefreshToken(fakeRefreshToken)).thenReturn(Optional.of(token));

        doNothing().when(refreshTokenRepos).deleteById(token.getId());

        cut.deleteRefreshTokenByRefreshToken(fakeRefreshToken);

        verify(refreshTokenRepos).findByRefreshToken(fakeRefreshToken);
        verify(refreshTokenRepos).deleteById(token.getId());

    }

    @Test
    @DisplayName("Test deleteRefreshTokenByRefreshToken throws an excepiton")
    void delete_refresh_token_by_refresh_token_should_throws_an_exception() {
        String fakeRefreshToken = "fake-refresh-token";

        when(refreshTokenRepos.findByRefreshToken(fakeRefreshToken)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            cut.deleteRefreshTokenByRefreshToken(fakeRefreshToken);
        });

        assertEquals("refresh token not found with token -> " + fakeRefreshToken, ex.getMessage());
    }

    @Test
    @DisplayName("Test generateRefreshToken")
    void should_generate_refresh_token_by_user_id() {

        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        when(userService.findById(userId)).thenReturn(Optional.of(user));
        when(tokenProvider.generateRefreshToken(userId)).thenReturn("fake-refresh-token");

        RefreshToken savedToken = new RefreshToken();
        savedToken.setRefreshToken("fake-refresh-token");
        savedToken.setUser(user);
        when(refreshTokenRepos.save(savedToken)).thenReturn(savedToken);

        RefreshToken result = cut.generateRefreshToken(userId);

        assertNotNull(result);
        assertEquals(savedToken, result);
        assertEquals(user.getId(), savedToken.getUser().getId());
        assertEquals(savedToken.getRefreshToken(), result.getRefreshToken());

        verify(userService).findById(userId);
        verify(tokenProvider).generateRefreshToken(userId);
        verify(refreshTokenRepos).save(savedToken);

    }

    @Test
    @DisplayName("Test generateRefreshToken should return null")
    void generate_refresh_token_by_user_id_should_return_null() {
        Long userId = 1L;

        when(userService.findById(userId)).thenReturn(Optional.empty());

        RefreshToken result = cut.generateRefreshToken(userId);

        assertNull(result);
        verify(userService).findById(userId);
    }

}
