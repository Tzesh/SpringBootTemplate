package com.tzesh.springtemplate.controller;

import com.tzesh.springtemplate.Application;
import com.tzesh.springtemplate.controller.base.controller.ControllerTest;
import com.tzesh.springtemplate.controller.base.response.SimplifiedResponse;
import com.tzesh.springtemplate.controller.utils.JwtUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;

/**
 * UserControllerTest class for testing UserController
 * @author tzesh
 */
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(2)
public class UserControllerTest extends ControllerTest {

    @Autowired
    TestRestTemplate template;

    public UserControllerTest() {
        super("/api/v1/users/");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "1", "current"})
    @Order(0)
    public void should_return_unauthorized(String path) {
        SimplifiedResponse response = template.getForObject(BASE_URL + path, SimplifiedResponse.class);

        assert !response.isSuccess();
        assert response.getStatus().equalsIgnoreCase(HttpStatus.UNAUTHORIZED.toString());
    }

    @Test
    @Order(1)
    public void should_get_current_user() {
        // add authorization header
        JwtUtil.addAuthorizationHeader(headers);

        // get current user
        SimplifiedResponse response = template.getForObject(BASE_URL + "current", SimplifiedResponse.class);

        // assert response
        assert !response.isSuccess();
        assert response.getStatus().equalsIgnoreCase(HttpStatus.UNAUTHORIZED.toString());
    }
}
