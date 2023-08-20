package com.sinandemir.todoapp.services;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sinandemir.todoapp.dto.requests.UserLoginRequest;
import com.sinandemir.todoapp.dto.requests.UserRegisterRequest;
import com.sinandemir.todoapp.dto.responses.UserLoginResponse;
import com.sinandemir.todoapp.dto.responses.UserRegisterResponse;
import com.sinandemir.todoapp.entities.RefreshToken;
import com.sinandemir.todoapp.entities.Role;
import com.sinandemir.todoapp.entities.User;
import com.sinandemir.todoapp.exceptions.TodoGlobalException;
import com.sinandemir.todoapp.repositories.RoleRepository;
import com.sinandemir.todoapp.repositories.UserRepository;
import com.sinandemir.todoapp.security.JwtTokenProvider;

@Service
public class AuthService {

    private UserRepository userRepos;
    private RoleRepository roleRepos;
    private PasswordEncoder passwordEncoder;
    private ModelMapper modelMapper;
    private AuthenticationManager authenticationManager;
    private JwtTokenProvider jwtTokenProvider;
    private RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepos, RoleRepository roleRepos, PasswordEncoder passwordEncoder,
            ModelMapper modelMapper, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider,
            RefreshTokenService refreshTokenService) {
        this.userRepos = userRepos;
        this.roleRepos = roleRepos;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
    }

    public UserRegisterResponse register(UserRegisterRequest registerRequest) {

        if (userRepos.existsByUsername(registerRequest.getUsername())) {
            throw new TodoGlobalException(HttpStatus.BAD_REQUEST, "username is already exist!");
        }

        if (userRepos.existsByEmail(registerRequest.getEmail())) {
            throw new TodoGlobalException(HttpStatus.BAD_REQUEST, "email is already exist!");
        }

        User user = new User();
        user.setName(registerRequest.getName());
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepos.findByName("ROLE_USER");
        roles.add(userRole);

        user.setRoles(roles);

        User savedUser = userRepos.save(user);
        UserRegisterResponse mappedUser = modelMapper.map(savedUser, UserRegisterResponse.class);
        return mappedUser;
    }

    public UserLoginResponse login(UserLoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(loginRequest.getUsernameOrEmail());

        Optional<User> userOptional = userRepos.findByUsernameOrEmail(loginRequest.getUsernameOrEmail(),
                loginRequest.getUsernameOrEmail());

        String role = null;
        Long userId = null;
        UserLoginResponse loginResponse = new UserLoginResponse();

        if (userOptional.isPresent()) {
            User loggedInUser = userOptional.get();
            userId = loggedInUser.getId();
            RefreshToken refreshToken = refreshTokenService.getRefreshTokenByUserId(userId);
            Optional<Role> roleOptional = loggedInUser.getRoles().stream().findFirst();

            if (refreshToken != null) {
                loginResponse.setRefreshToken(refreshToken.getRefreshToken());
            } else {
                refreshToken = refreshTokenService.generateRefreshToken(userId);
                loginResponse.setRefreshToken(refreshToken.getRefreshToken());
            }

            if (roleOptional.isPresent()) {
                Role userRole = roleOptional.get();
                role = userRole.getName();
            }
        }

        loginResponse.setAccessToken(token);
        loginResponse.setRole(role);
        loginResponse.setUserId(userId);
        return loginResponse;
    }

    public String refreshAccessToken(String refreshToken) {
        Long userId = jwtTokenProvider.getUserId(refreshToken);
        Optional<User> user = userRepos.findById(userId);
        if (jwtTokenProvider.validateToken(refreshToken)) {
            String newJwtToken = jwtTokenProvider.generateToken(user.get().getUsername());
            return newJwtToken;
        }
        return null;
    }
}
