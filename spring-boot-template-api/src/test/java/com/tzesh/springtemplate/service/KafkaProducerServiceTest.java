package com.tzesh.springtemplate.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.verify;

class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private KafkaProducerService kafkaProducerService;

    public KafkaProducerServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendMessage_shouldSendToKafka() {
        String message = "test message";
        kafkaProducerService.sendMessage(message);
        verify(kafkaTemplate).send("test-topic", message);
    }
}

