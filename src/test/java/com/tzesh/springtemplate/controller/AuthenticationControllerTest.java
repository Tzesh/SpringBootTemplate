package com.tzesh.springtemplate.controller;

import com.tzesh.springtemplate.Application;
import com.tzesh.springtemplate.base.controller.ControllerTest;
import com.tzesh.springtemplate.base.response.SimplifiedResponse;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

/**
 * AuthenticationControllerTest class for testing AuthenticationController
 *
 * @author tzesh
 */
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationControllerTest extends ControllerTest {
    @Autowired
    TestRestTemplate template;

    public AuthenticationControllerTest() {
        super("/auth/");
    }

    @BeforeEach
    public void setup() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    public void should_register_user() throws Exception {
        JSONObject content = new JSONObject()
                .put("username", "tzesh")
                .put("password", "12345678")
                .put("email", "mail@ugurdindar.com")
                .put("name", "Uğur Dindar");

        HttpEntity<String> request = new HttpEntity<>(content.toString(), headers);

        SimplifiedResponse response = template.postForObject(BASE_URL + "register",
                request,
                SimplifiedResponse.class
        );

        assert response.isSuccess();
        assert response.getStatus().equalsIgnoreCase("CREATED");
    }
}
