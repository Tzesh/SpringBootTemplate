package com.tzesh.springtemplate.service.auth;

import com.tzesh.springtemplate.request.auth.RegisterRequest;
import com.tzesh.springtemplate.response.AuthenticationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {
    @Mock
    private JwtService jwtService;
    @Mock
    private com.tzesh.springtemplate.repository.user.UserRepository userRepository;
    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    @Mock
    private com.tzesh.springtemplate.repository.auth.TokenRepository tokenRepository;
    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Mock repository method used in register
        when(userRepository.existsByUsernameOrEmail(anyString(), anyString())).thenReturn(false);
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("encodedPassword");
        when(tokenRepository.save(any())).thenReturn(null); // Prevent NPE
        // Mock any other required repository/service calls for register
        // For example, if your service calls userRepository.save, mock it:
        when(userRepository.save(any())).thenReturn(new com.tzesh.springtemplate.entity.User());
    }

    @Test
    void registerShouldReturnAuthenticationResponse() {
        // Mock all required dependencies for register
        RegisterRequest request = new RegisterRequest("user", "password", "email@mail.com", "Name Surname");
        // Mock JWT service if needed
        when(jwtService.generateToken(any())).thenReturn("dummy-jwt-token");
        // Mock any other repository/service calls as needed
        assertDoesNotThrow(() -> {
            AuthenticationResponse result = authenticationService.register(request);
            assertNotNull(result);
        });
    }
}
