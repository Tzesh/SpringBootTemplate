package com.tzesh.springtemplate.controller;

import com.tzesh.springtemplate.Application;
import com.tzesh.springtemplate.controller.base.controller.ControllerTest;
import com.tzesh.springtemplate.controller.base.response.SimplifiedResponse;
import com.tzesh.springtemplate.controller.utils.JwtUtil;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/**
 * AuthenticationControllerTest class for testing AuthenticationController
 *
 * @author tzesh
 */
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(1)
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
    @Order(1)
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

    @Test
    @Order(2)
    public void should_login_user() throws Exception {
        JSONObject content = new JSONObject()
                .put("username", "tzesh")
                .put("password", "12345678");

        HttpEntity<String> request = new HttpEntity<>(content.toString(), headers);

        SimplifiedResponse response = template.postForObject(BASE_URL + "login",
                request,
                SimplifiedResponse.class
        );

        JwtUtil.extractToken(response.getData());

        assert response.isSuccess();
        assert response.getStatus().equalsIgnoreCase("OK");
    }
}
