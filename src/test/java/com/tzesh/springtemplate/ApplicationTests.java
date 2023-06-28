package com.tzesh.springtemplate;

import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {Application.class})
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class ApplicationTests {

    @Test
    void contextLoads() {
    }

}
