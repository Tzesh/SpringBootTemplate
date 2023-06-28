package com.tzesh.springtemplate.service.auth;

import com.tzesh.springtemplate.base.entity.field.BaseAuditableFields;
import com.tzesh.springtemplate.base.error.GenericErrorMessage;
import com.tzesh.springtemplate.base.exception.BaseException;
import com.tzesh.springtemplate.base.exception.NotFoundException;
import com.tzesh.springtemplate.entity.auth.Token;
import com.tzesh.springtemplate.entity.User;
import com.tzesh.springtemplate.enums.auth.RoleEnum;
import com.tzesh.springtemplate.enums.auth.TokenEnum;
import com.tzesh.springtemplate.repository.auth.TokenRepository;
import com.tzesh.springtemplate.repository.UserRepository;
import com.tzesh.springtemplate.request.auth.AuthorizationRequest;
import com.tzesh.springtemplate.request.auth.LoginRequest;
import com.tzesh.springtemplate.request.auth.RegisterRequest;
import com.tzesh.springtemplate.response.auth.AuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
    public AuthenticationResponse register(RegisterRequest request) {
        // check if username or email already exists
        if (repository.existsByUsernameOrEmail(request.username(), request.email())) {
            throw new BaseException(
                    GenericErrorMessage.builder()
                            .message("Username or email already exists")
                            .build()
                    );
        }

        // create user
        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .name(request.name())
                .password(passwordEncoder.encode(request.password()))
                .role(RoleEnum.USER)
                .build();

        // create auditable fields
        BaseAuditableFields auditableFields = new BaseAuditableFields();

        // set created by
        auditableFields.setCreatedBy(user.getUsername());

        // set created date
        auditableFields.setCreatedDate(LocalDateTime.now());

        // set base auditable fields
        user.setAuditableFields(auditableFields);

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
     *
     * @param request Login request
     * @return AuthenticationResponse
     * @see LoginRequest
     */
    public AuthenticationResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        User user = repository.findByUsername(request.username())
                .orElseThrow(
                        () -> new NotFoundException(
                                GenericErrorMessage.builder()
                                        .message("User not found with username: " + request.username())
                                        .build()
                        )
                );

        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

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
    private void saveUserToken(User user, String jwtToken) {
        Token token = Token.builder()
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
     *
     * @param user User
     */
    private void revokeAllUserTokens(User user) {
        // get all valid user tokens
        List<Token> validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());

        // check if valid user tokens is empty
        if (validUserTokens.isEmpty())
            return;

        // revoke all valid user tokens
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });

        // save all valid user tokens
        tokenRepository.saveAll(validUserTokens);
    }

    /**
     * Refresh token
     *
     * @param request  HTTP request
     * @param response HTTP response
     * @throws IOException IOException
     */
    public AuthenticationResponse refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // get authorization header
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // get refresh token and username
        final String refreshToken;
        final String username;

        // check if authorization header is null or not bearer
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // return if authorization header is null or not bearer
            return null;
        }

        // get refresh token and username
        refreshToken = authHeader.substring(7);
        username = jwtService.extractUsername(refreshToken);

        // check if username is null
        if (username != null) {
            // get user
            User user = this.repository.findByUsername(username)
                    .orElseThrow(
                            () -> new NotFoundException(
                                    GenericErrorMessage.builder()
                                            .message("User not found with username: " + username)
                                            .build()
                            )
                    );

            // check if refresh token is valid
            if (jwtService.isTokenValid(refreshToken, user)) {
                // generate new access token
                String accessToken = jwtService.generateToken(user);

                // revoke all user tokens
                revokeAllUserTokens(user);

                // save new refresh token
                saveUserToken(user, accessToken);

                // return response
                return AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
            }
        }

        // return null if username is null
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
    public AuthenticationResponse authorize(AuthorizationRequest request) {
        // check if given secret is correct
        if (!request.secret().equals(authorizationKey)) {
            throw new RuntimeException("Secret is not correct");
        }

        // get user
        User user = repository.findByUsername(request.username())
                .orElseThrow(
                        () -> new NotFoundException(
                                GenericErrorMessage.builder()
                                        .message("User not found with username: " + request.username())
                                        .build()
                        )
                );

        // set role of user
        user.setRole(request.role());

        // get auditable fields
        BaseAuditableFields auditableFields = user.getAuditableFields();

        // set updated by
        auditableFields.setUpdatedBy("SYSTEM");

        // set updated date
        auditableFields.setUpdatedDate(LocalDateTime.now());

        // save user
        user = repository.save(user);

        // generate jwt token
        String jwtToken = jwtService.generateToken(user);

        // generate refresh token
        String refreshToken = jwtService.generateRefreshToken(user);

        // save refresh token
        saveUserToken(user, jwtToken);

        // return response
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

}
