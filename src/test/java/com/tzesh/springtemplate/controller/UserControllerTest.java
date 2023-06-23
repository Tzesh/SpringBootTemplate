package com.tzesh.springtemplate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tzesh.springtemplate.Application;
import com.tzesh.springtemplate.base.controller.ControllerTest;
import com.tzesh.springtemplate.base.response.SimplifiedResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;

/**
 * UserControllerTest class for testing UserController
 * @author tzesh
 */
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest extends ControllerTest {

    @Autowired
    TestRestTemplate template;

    public UserControllerTest() {
        super("/users/");
    }

    @Test
    void get_all_users_should_return_unauthorized() {
        SimplifiedResponse response = template.getForObject(BASE_URL, SimplifiedResponse.class);

        assert !response.isSuccess();
        assert response.getStatus().equalsIgnoreCase(HttpStatus.UNAUTHORIZED.toString());
    }
}
