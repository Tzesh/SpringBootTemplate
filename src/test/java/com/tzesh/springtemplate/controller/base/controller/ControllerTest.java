package com.tzesh.springtemplate.controller.base.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.MappedSuperclass;
import org.springframework.http.HttpHeaders;


/**
 * @author tzesh
 */
@MappedSuperclass
public abstract class ControllerTest {
    protected final String BASE_URL;
    protected ObjectMapper objectMapper;
    @SuppressWarnings("JpaAttributeTypeInspection")
    protected HttpHeaders headers = new HttpHeaders();

    public ControllerTest(String BASE_URL) {
        this.BASE_URL = BASE_URL;
    }
}
