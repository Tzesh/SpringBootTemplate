package com.tzesh.springtemplate.utils;

import lombok.Data;
import org.springframework.http.HttpHeaders;

/**
 * JWT utility class to store the JWT token and add/remove it from the HTTP headers.
 * @author tzesh
 */
@Data
public class JwtUtil {
    private static String accessToken;

    /**
     * Add the JWT token to the HTTP headers
     * @param headers HTTP headers
     * @return HTTP headers with JWT token
     */
    public static HttpHeaders addAuthorizationHeader(HttpHeaders headers) {
        headers.add("Authorization", "Bearer " + accessToken);
        return headers;
    }

    /**
     * Remove the JWT token from the HTTP headers
     * @param headers HTTP headers
     * @return HTTP headers without JWT token
     */
    public static HttpHeaders removeAuthorizationHeader(HttpHeaders headers) {
        headers.remove("Authorization");
        return headers;
    }
}
