package com.tzesh.springtemplate.controller.kafka;

import com.tzesh.springtemplate.service.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(name = "Kafka Controller", description = "Endpoints for testing Kafka integration.")
@RestController
@RequestMapping("/kafka")
public class KafkaController {
    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Operation(summary = "Send a message to Kafka", description = "Sends a message to the 'test-topic' topic in Kafka. Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Message sent successfully"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN role allowed"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/send")
    @PreAuthorize("hasRole('ADMIN')")
    public String sendMessage(@Parameter(description = "Message to send to Kafka", required = true)
                             @RequestParam String message) {
        kafkaProducerService.sendMessage(message);
        return "Message sent to Kafka: " + message;
    }
}

