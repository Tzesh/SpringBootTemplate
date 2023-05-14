package com.tzesh.springtemplate.service.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tzesh.springtemplate.entity.auth.Token;
import com.tzesh.springtemplate.entity.User;
import com.tzesh.springtemplate.enumerator.auth.RoleEnum;
import com.tzesh.springtemplate.enumerator.auth.TokenEnum;
import com.tzesh.springtemplate.repository.auth.TokenRepository;
import com.tzesh.springtemplate.repository.UserRepository;
import com.tzesh.springtemplate.request.auth.LoginRequest;
import com.tzesh.springtemplate.request.auth.RegisterRequest;
import com.tzesh.springtemplate.response.auth.AuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Authentication service for login and register
 * @see AuthenticationService
 * @see AuthenticationManager
 * @see PasswordEncoder
 * @see JwtService
 * @see UserRepository
 * @see TokenRepository
 * @author tzesh
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Register user
     * @param request Register request
     * @return AuthenticationResponse
     */
    public AuthenticationResponse register(RegisterRequest request) {
        // check if username or email already exists
        if (repository.existsByUsernameOrEmail(request.username(), request.email())) {
            throw new RuntimeException("Username or email already exists");
        }

        // create user
        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .name(request.name())
                .password(passwordEncoder.encode(request.password()))
                .roleEnum(RoleEnum.USER)
                .build();

        // save user
        User savedUser = repository.save(user);

        // generate jwt token
        String jwtToken = jwtService.generateToken(user);

        // generate refresh token
        String refreshToken = jwtService.generateRefreshToken(user);

        // save refresh token
        saveUserToken(savedUser, jwtToken);

        // return response
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Login user
     * @param request Login request
     * @return AuthenticationResponse
     */
    public AuthenticationResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        var user = repository.findByUsername(request.username())
                .orElseThrow();

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Save user token
     * @param user User
     * @param jwtToken JWT token
     */
    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenEnum.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    /**
     * Revoke all user tokens
     * @param user User
     */
    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    /**
     * Refresh token
     * @param request HTTP request
     * @param response HTTP response
     * @throws IOException IOException
     */
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        // get authorization header
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // get refresh token and username
        final String refreshToken;
        final String username;

        // check if authorization header is null or not bearer
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            // return if authorization header is null or not bearer
            return;
        }

        // get refresh token and username
        refreshToken = authHeader.substring(7);
        username = jwtService.extractUsername(refreshToken);

        // check if username is null
        if (username != null) {
            // get user
            var user = this.repository.findByUsername(username)
                    .orElseThrow();

            // check if refresh token is valid
            if (jwtService.isTokenValid(refreshToken, user)) {
                // generate new access token
                var accessToken = jwtService.generateToken(user);

                // revoke all user tokens
                revokeAllUserTokens(user);

                // save new refresh token
                saveUserToken(user, accessToken);

                // generate authentication response
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();

                // write response
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}
