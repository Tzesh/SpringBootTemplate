package com.tzesh.springtemplate.service.auth;

import com.tzesh.springtemplate.repository.auth.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

/**
 * Logout service to revoke token
 * @see LogoutHandler
 * @see TokenRepository
 * @author tzesh
 */
@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;

    /**
     * Revoke token
     * @param request Request
     * @param response Response
     * @param authentication Authentication
     */
    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        // get jwt token from header
        final String authHeader = request.getHeader("Authorization");
        final String jwt;

        // check if token is null or not started with Bearer
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }

        // get jwt token
        jwt = authHeader.substring(7);

        // get stored token
        var storedToken = tokenRepository.findByToken(jwt)
                .orElse(null);

        // check if token is null
        if (storedToken != null) {
            // revoke token
            storedToken.setExpired(true);
            storedToken.setRevoked(true);

            // save token
            tokenRepository.save(storedToken);

            // clear context
            SecurityContextHolder.clearContext();
        }
    }
}
