package com.tzesh.springtemplate.controller.auth;

import com.tzesh.springtemplate.base.response.BaseResponse;
import com.tzesh.springtemplate.response.auth.AuthenticationResponse;
import com.tzesh.springtemplate.request.auth.AuthorizationRequest;
import com.tzesh.springtemplate.request.auth.LoginRequest;
import com.tzesh.springtemplate.request.auth.RegisterRequest;
import com.tzesh.springtemplate.service.auth.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author tzesh
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
@Tag(name = "1. Authentication Controller", description = "Authentication operations for users")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Register a new user with the given details and return the authentication response")
    public ResponseEntity<BaseResponse<AuthenticationResponse>> register(@RequestBody @Valid RegisterRequest request) {
        // call the register method in the authentication service
        AuthenticationResponse response = authenticationService.register(request);

        // return the response
        return BaseResponse.created(response).message("User registered successfully").build();
    }
    @PostMapping("/login")
    @Operation(summary = "Login a user", description = "Login a user with the given details and return the authentication response")
    public ResponseEntity<BaseResponse<AuthenticationResponse>> login(@RequestBody @Valid LoginRequest request) {
        // call the login method in the authentication service
        AuthenticationResponse response = authenticationService.login(request);

        // return the response
        return BaseResponse.ok(response).message("User logged in successfully").build();
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh the authentication token", description = "Refresh the authentication token and return the authentication response")
    public ResponseEntity<BaseResponse<AuthenticationResponse>> refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // call the refresh token method in the authentication service
        AuthenticationResponse refreshToken = authenticationService.refreshToken(request, response);

        // return the response
        return BaseResponse.ok(refreshToken).message("Token refreshed successfully").build();
    }

    @PostMapping("/authorize")
    @Operation(summary = "Authorize the user", description = "Authorize the user and return the authentication response")
    public ResponseEntity<BaseResponse<AuthenticationResponse>> authorize(@RequestBody @Valid AuthorizationRequest request) {
        // call the login method in the authentication service
        AuthenticationResponse response = authenticationService.authorize(request);

        // return the response
        return BaseResponse.ok(response).message("User authorized successfully").build();
    }
}
