package com.tzesh.springtemplate.config.security.jwt;

import com.tzesh.springtemplate.base.error.GenericErrorMessage;
import com.tzesh.springtemplate.base.response.BaseResponse;
import io.swagger.v3.core.util.Json;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author tzesh
 */
@Component
@Slf4j
public class AuthenticationEntryPoint implements org.springframework.security.web.AuthenticationEntryPoint {

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

        // create error message
        GenericErrorMessage genericErrorMessage = GenericErrorMessage.builder()
                .details(authException.getMessage())
                .path(request.getRequestURI())
                .message("Unauthorized to access this resource")
                .build();

        // write response
        response.getWriter().write(
                Json.pretty(
                        BaseResponse.builder()
                                .status(HttpStatus.UNAUTHORIZED)
                                .data(
                                        genericErrorMessage
                                )
                                .message("Unauthorized to access this resource")
                                .build()
                )
        );

        // log error
        log.error("Unauthorized to access this resource: {}", genericErrorMessage);

        // flush writer
        response.getWriter().flush();

        // close writer
        response.getWriter().close();
    }
}
