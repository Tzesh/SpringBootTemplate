package com.tzesh.springtemplate.response;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthenticationResponseTest {
    @Test
    void testTokenGetterSetter() {
        AuthenticationResponse response = new AuthenticationResponse();
        response.setAccessToken("abc123");
        assertEquals("abc123", response.getAccessToken());
    }

    @Test
    void testEqualsAndHashCode() {
        AuthenticationResponse r1 = new AuthenticationResponse();
        r1.setAccessToken("token");
        AuthenticationResponse r2 = new AuthenticationResponse();
        r2.setAccessToken("token");
        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }
}

