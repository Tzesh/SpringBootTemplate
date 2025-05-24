package com.tzesh.springtemplate.service.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class LogoutServiceTest {
    @Mock
    private JwtService jwtService;
    @InjectMocks
    private LogoutService logoutService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLogoutHandlerDoesNotThrow() {
        var request = mock(jakarta.servlet.http.HttpServletRequest.class);
        var response = mock(jakarta.servlet.http.HttpServletResponse.class);
        var authentication = mock(org.springframework.security.core.Authentication.class);
        assertDoesNotThrow(() -> logoutService.logout(request, response, authentication));
    }
}
