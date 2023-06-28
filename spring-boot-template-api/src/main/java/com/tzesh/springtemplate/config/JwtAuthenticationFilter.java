package com.tzesh.springtemplate.config;

import com.tzesh.springtemplate.repository.auth.TokenRepository;
import com.tzesh.springtemplate.service.auth.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter class for authenticating JWT tokens
 * and setting the authentication in the Security Context
 * @see OncePerRequestFilter
 * @see JwtService
 * @see UserDetailsService
 * @see TokenRepository
 * @author tzesh
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;

    /**
     * Method that filters the requests and authenticates the user
     * @param request request HttpServletRequest
     * @param response response HttpServletResponse
     * @param filterChain filterChain that contains the filters
     * @throws ServletException if the servlet encounters difficulty
     * @throws IOException if the servlet encounters difficulty
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        // if the request is for authentication, skip the filter
        if (request.getServletPath().contains("/api/v1/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        // get the token from the request header
        final String authHeader = request.getHeader("Authorization");

        // initialize the token and username
        final String jwt;
        final String username;

        // if the token is not present or does not start with "Bearer ", skip the filter
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // extract the token from the header
        jwt = authHeader.substring(7);

        // extract the username from the token
        username = jwtService.extractUsername(jwt);

        // if the username is not null and the authentication context is null, authenticate the user
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // load the user details from the database
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // check if the token is valid and not revoked
            var isTokenValid = tokenRepository.findByToken(jwt)
                    .map(t -> !t.isExpired() && !t.isRevoked())
                    .orElse(false);

            // if the token is valid, set the authentication context
            if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
                // create the authentication token
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                // set the authentication details
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // set the authentication context
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // continue the filter chain
        filterChain.doFilter(request, response);
    }
}
