package com.tzesh.springtemplate.service.auth;

import com.tzesh.springtemplate.base.entity.field.BaseAuditableFields;
import com.tzesh.springtemplate.base.error.GenericErrorMessage;
import com.tzesh.springtemplate.base.exception.BaseException;
import com.tzesh.springtemplate.base.exception.NotFoundException;
import com.tzesh.springtemplate.entity.User;
import com.tzesh.springtemplate.enumeration.auth.Role;
import com.tzesh.springtemplate.enumeration.auth.Token;
import com.tzesh.springtemplate.repository.auth.TokenRepository;
import com.tzesh.springtemplate.repository.user.UserRepository;
import com.tzesh.springtemplate.request.auth.AuthorizationRequest;
import com.tzesh.springtemplate.request.auth.LoginRequest;
import com.tzesh.springtemplate.request.auth.RegisterRequest;
import com.tzesh.springtemplate.response.AuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Authentication service for login and register
 *
 * @author tzesh
 * @see AuthenticationService
 * @see AuthenticationManager
 * @see PasswordEncoder
 * @see JwtService
 * @see UserRepository
 * @see TokenRepository
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    @Value("${security.jwt.authorization-key}")
    private String authorizationKey;
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Register user
     *
     * @param request Register request
     * @return AuthenticationResponse
     * @throws RuntimeException if username or email already exists
     * @see RegisterRequest
     */
    public AuthenticationResponse register(final RegisterRequest request) {
        if (repository.existsByUsernameOrEmail(request.username(), request.email())) {
            throw new BaseException(
                    GenericErrorMessage.builder()
                            .message("Username or email already exists")
                            .build()
            );
        }

        final User user = User.builder()
                .username(request.username())
                .email(request.email())
                .name(request.name())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();
        final BaseAuditableFields auditableFields = new BaseAuditableFields();
        auditableFields.setCreatedBy(user.getUsername());
        auditableFields.setCreatedDate(LocalDateTime.now());
        user.setAuditableFields(auditableFields);

        final User savedUser = repository.save(user);
        final String jwtToken = jwtService.generateToken(user);
        final String refreshToken = jwtService.generateRefreshToken(user);

        saveUserToken(savedUser, jwtToken);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Login user
     *
     * @param request Login request
     * @return AuthenticationResponse
     * @see LoginRequest
     */
    public AuthenticationResponse login(final LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        final User user = repository.findByUsername(request.username())
                .orElseThrow(
                        () -> new NotFoundException(
                                GenericErrorMessage.builder()
                                        .message("User not found with username: " + request.username())
                                        .build()
                        )
                );

        final String jwtToken = jwtService.generateToken(user);
        final String refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Save user token
     *
     * @param user     User
     * @param jwtToken JWT token
     */
    private void saveUserToken(final User user, final String jwtToken) {
        final com.tzesh.springtemplate.entity.auth.Token token = com.tzesh.springtemplate.entity.auth.Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(Token.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    /**
     * Revoke all user tokens
     *
     * @param user User
     */
    private void revokeAllUserTokens(final User user) {
        final List<com.tzesh.springtemplate.entity.auth.Token> validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
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
     *
     * @param request  HTTP request
     * @param response HTTP response
     */
    public AuthenticationResponse refreshToken(final HttpServletRequest request, final HttpServletResponse response) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String username;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        refreshToken = authHeader.substring(7);
        username = jwtService.extractUsername(refreshToken);
        if (username != null) {
            final User user = this.repository.findByUsername(username)
                    .orElseThrow(
                            () -> new NotFoundException(
                                    GenericErrorMessage.builder()
                                            .message("User not found with username: " + username)
                                            .build()
                            )
                    );
            if (jwtService.isTokenValid(refreshToken, user)) {
                final String accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                return AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
            }
        }
        return null;
    }

    /**
     * Authorize user to the role
     *
     * @param request Authorization request
     * @return AuthenticationResponse
     * @see AuthorizationRequest
     * @see AuthenticationResponse
     */
    public AuthenticationResponse authorize(final AuthorizationRequest request) {
        if (!request.secret().equals(authorizationKey)) {
            throw new RuntimeException("Secret is not correct");
        }

        final User user = repository.findByUsername(request.username())
                .orElseThrow(
                        () -> new NotFoundException(
                                GenericErrorMessage.builder()
                                        .message("User not found with username: " + request.username())
                                        .build()
                        )
                );
        user.setRole(request.role());

        final BaseAuditableFields auditableFields = user.getAuditableFields();
        auditableFields.setUpdatedBy("SYSTEM");
        auditableFields.setUpdatedDate(LocalDateTime.now());

        final User updatedUser = repository.save(user);
        final String jwtToken = jwtService.generateToken(updatedUser);
        final String refreshToken = jwtService.generateRefreshToken(updatedUser);
        saveUserToken(updatedUser, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

}
