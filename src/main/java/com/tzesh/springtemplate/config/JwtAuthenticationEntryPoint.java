package com.tzesh.springtemplate.config;

import com.tzesh.springtemplate.base.error.GenericErrorMessage;
import com.tzesh.springtemplate.base.response.BaseResponse;
import io.swagger.v3.core.util.Json;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author tzesh
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Method that handles the authentication entry point
     * @param request Request
     * @param response Response
     * @param authException AuthenticationException
     * @throws IOException if the servlet encounters difficulty
     * @throws ServletException if the servlet encounters difficulty
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // set response status to 401
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // set response content type
        response.setContentType("application/json");

        // write response
        response.getWriter().write(
                Json.pretty(
                        BaseResponse.builder()
                                .status(HttpStatus.UNAUTHORIZED)
                                .data(
                                        GenericErrorMessage.builder()
                                                .details(authException.getMessage())
                                                .path(request.getRequestURI())
                                                .message("Unauthorized to access this resource")
                                                .build()
                                )
                                .message("Unauthorized to access this resource")
                                .build()
                )
        );

        // flush writer
        response.getWriter().flush();

        // close writer
        response.getWriter().close();
    }
}
