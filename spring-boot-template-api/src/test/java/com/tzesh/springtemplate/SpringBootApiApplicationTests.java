package com.tzesh.springtemplate;

import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.TestClassOrder;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = SpringBootApiApplication.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class SpringBootApiApplicationTests {
}


