package com.sinandemir.todoapp.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.sinandemir.todoapp.dto.requests.UserLoginRequest;
import com.sinandemir.todoapp.dto.requests.UserRegisterRequest;
import com.sinandemir.todoapp.dto.responses.UserLoginResponse;
import com.sinandemir.todoapp.dto.responses.UserRegisterResponse;
import com.sinandemir.todoapp.entities.RefreshToken;
import com.sinandemir.todoapp.entities.Role;
import com.sinandemir.todoapp.entities.User;
import com.sinandemir.todoapp.exceptions.ResourceNotFoundException;
import com.sinandemir.todoapp.exceptions.TodoGlobalException;
import com.sinandemir.todoapp.security.JwtTokenProvider;

@SpringBootTest
public class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private RoleService roleService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService cut;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test register")
    void should_return_saved_user_by_register_request() {
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setUsername("someValue");
        registerRequest.setEmail("xxx@fake-email.com");
        registerRequest.setPassword("someValue");
        registerRequest.setName("someValue");

        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setUsername(registerRequest.getUsername());
        user.setName(registerRequest.getName());
        user.setPassword("encoded-password");

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");

        Set<Role> roles = new HashSet<Role>();
        roles.add(role);

        user.setRoles(roles);

        UserRegisterResponse registerResponse = new UserRegisterResponse();
        registerResponse.setEmail(user.getEmail());
        registerResponse.setUsername(user.getUsername());
        registerResponse.setName(user.getName());
        registerResponse.setId(1L);

        when(userService.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(userService.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encoded-password");
        when(roleService.findByName("ROLE_USER")).thenReturn(role);
        when(userService.save(user)).thenReturn(user);
        when(modelMapper.map(user, UserRegisterResponse.class)).thenReturn(registerResponse);

        UserRegisterResponse result = cut.register(registerRequest);

        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getUsername(), result.getUsername());

        verify(userService).existsByEmail(registerRequest.getEmail());
        verify(userService).existsByUsername(registerRequest.getUsername());
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(roleService).findByName("ROLE_USER");
        verify(userService).save(user);
        verify(modelMapper).map(user, UserRegisterResponse.class);

    }

    @Test
    @DisplayName("Test register throws an exception by existsByUsername")
    void should_register_throws_an_exception_by_exists_by_username() {
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setUsername("someValue");

        when(userService.existsByUsername(registerRequest.getUsername())).thenReturn(true);

        TodoGlobalException ex = assertThrows(TodoGlobalException.class, () -> {
            cut.register(registerRequest);
        });

        assertEquals("username is already exist!", ex.getMessage());

        verify(userService).existsByUsername(registerRequest.getUsername());
    }

    @Test
    @DisplayName("Test register throws an exception by existsByEmail")
    void should_register_throws_an_exception_by_exists_by_email() {
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setEmail("someValue");

        when(userService.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        TodoGlobalException ex = assertThrows(TodoGlobalException.class, () -> {
            cut.register(registerRequest);
        });

        assertEquals("email is already exist!", ex.getMessage());

        verify(userService).existsByEmail(registerRequest.getEmail());
    }

    @Test
    @DisplayName("Test refreshAccessToken")
    void should_refresh_access_token_return_new_access_token_by_refresh_token() {
        String refreshToken = "fake-refresh-token";
        String jwtToken = "fake-jwt-token";
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("someValue");

        when(tokenProvider.getUserId(refreshToken)).thenReturn(userId);
        when(userService.findById(userId)).thenReturn(Optional.of(user));
        when(tokenProvider.validateToken(refreshToken)).thenReturn(true);
        when(tokenProvider.generateToken(user.getUsername())).thenReturn(jwtToken);

        String result = cut.refreshAccessToken(refreshToken);

        assertNotNull(result);
        assertEquals(jwtToken, result);

        verify(tokenProvider).getUserId(refreshToken);
        verify(userService).findById(userId);
        verify(tokenProvider).validateToken(refreshToken);
        verify(tokenProvider).generateToken(user.getUsername());

    }

    @Test
    @DisplayName("Test refreshAccessToken return null")
    void should_refresh_access_token_return_null() {
        String refreshToken = "fake-refresh-token";
        Long userId = 1L;
        User user = new User();
        user.setUsername("someValue");
        user.setId(userId);

        when(tokenProvider.getUserId(refreshToken)).thenReturn(userId);
        when(userService.findById(userId)).thenReturn(Optional.of(user));
        when(tokenProvider.validateToken(refreshToken)).thenReturn(false);

        String result = cut.refreshAccessToken(refreshToken);

        assertNull(result);

        verify(tokenProvider).getUserId(refreshToken);
        verify(userService).findById(userId);
        verify(tokenProvider).validateToken(refreshToken);
    }

    @Test
    @DisplayName("Test login")
    void should_login_return_login_response_with_exists_refresh_token_by_login_request() {

        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setUsernameOrEmail("someValue");
        loginRequest.setPassword("someValue");

        String token = "fake-jwt-token";
        String refreshToken = "fake-refresh-token";
        Long userId = 1L;

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");

        Set<Role> roles = new HashSet<Role>();
        roles.add(role);

        User user = new User();
        user.setId(userId);
        user.setUsername(loginRequest.getUsernameOrEmail());
        user.setRoles(roles);

        RefreshToken refreshTokenObj = new RefreshToken();
        refreshTokenObj.setRefreshToken(refreshToken);
        refreshTokenObj.setUser(user);

        when(tokenProvider.generateToken(loginRequest.getUsernameOrEmail())).thenReturn(token);
        when(userService.findByUsernameOrEmail(
                loginRequest.getUsernameOrEmail(), loginRequest.getUsernameOrEmail()))
                .thenReturn(Optional.of(user));
        when(refreshTokenService.getRefreshTokenByUserId(userId)).thenReturn(refreshTokenObj);

        UserLoginResponse result = cut.login(loginRequest);

        assertNotNull(result);
        assertEquals(token, result.getAccessToken());
        assertEquals("ROLE_USER", result.getRole());
        assertEquals(userId, result.getUserId());
        assertEquals(refreshToken, result.getRefreshToken());

        verify(tokenProvider).generateToken(loginRequest.getUsernameOrEmail());
        verify(userService).findByUsernameOrEmail(
                loginRequest.getUsernameOrEmail(), loginRequest.getUsernameOrEmail());
        verify(refreshTokenService).getRefreshTokenByUserId(userId);
    }

    @Test
    @DisplayName("Test login")
    void should_login_return_login_response_with_new_refresh_token_by_login_request() {
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setUsernameOrEmail("someValue");
        loginRequest.setPassword("someValue");

        String token = "fake-jwt-token";
        String refreshToken = "fake-refresh-token";
        Long userId = 1L;

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");

        Set<Role> roles = new HashSet<Role>();
        roles.add(role);

        User user = new User();
        user.setId(userId);
        user.setUsername(loginRequest.getUsernameOrEmail());
        user.setRoles(roles);

        RefreshToken refreshTokenObj = new RefreshToken();
        refreshTokenObj.setRefreshToken(refreshToken);
        refreshTokenObj.setUser(user);

        when(tokenProvider.generateToken(loginRequest.getUsernameOrEmail())).thenReturn(token);
        when(userService.findByUsernameOrEmail(
                loginRequest.getUsernameOrEmail(), loginRequest.getUsernameOrEmail()))
                .thenReturn(Optional.of(user));
        when(refreshTokenService.getRefreshTokenByUserId(userId)).thenReturn(null);
        when(refreshTokenService.generateRefreshToken(userId)).thenReturn(refreshTokenObj);

        UserLoginResponse result = cut.login(loginRequest);

        assertNotNull(result);
        assertEquals(token, result.getAccessToken());
        assertEquals("ROLE_USER", result.getRole());
        assertEquals(userId, result.getUserId());
        assertEquals(refreshToken, result.getRefreshToken());

        verify(tokenProvider).generateToken(loginRequest.getUsernameOrEmail());
        verify(userService).findByUsernameOrEmail(
                loginRequest.getUsernameOrEmail(), loginRequest.getUsernameOrEmail());
        verify(refreshTokenService).getRefreshTokenByUserId(userId);
        verify(refreshTokenService).generateRefreshToken(userId);

    }

    @Test
    @DisplayName("Test login throws an exception")
    void should_login_throws_an_exception_by_user_not_found() {
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setUsernameOrEmail("someValue");
        loginRequest.setPassword("someValue");

        when(userService.findByUsernameOrEmail(loginRequest.getUsernameOrEmail(), loginRequest.getUsernameOrEmail()))
                .thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            cut.login(loginRequest);
        });

        assertEquals("user not found with username or email ->" + loginRequest.getUsernameOrEmail(), ex.getMessage());

        verify(userService).findByUsernameOrEmail(loginRequest.getUsernameOrEmail(), loginRequest.getUsernameOrEmail());
    }

}
