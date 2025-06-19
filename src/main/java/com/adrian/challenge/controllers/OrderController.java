package com.adrian.challenge.controllers;

import com.adrian.challenge.exceptions.CustomBadRequestException;
import com.adrian.challenge.models.Order;
import com.adrian.challenge.services.OrderService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/processOrder")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> processOrder(@Valid @RequestBody Order request) {
        log.info("Processing order for customer: {}", request.getCustomer());
        
        return orderService.processOrder(request)
                .thenApply(orderId -> {
                    log.info("Successfully processed order with ID: {}", orderId);
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "success");
                    response.put("orderId", orderId);
                    response.put("message", "Order processed successfully");
                    return ResponseEntity.status(HttpStatus.CREATED).body(response);
                })
                .exceptionally(ex -> {
                    log.error("Error processing order: {}", ex.getMessage(), ex);
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("status", "error");
                    errorResponse.put("message", "Failed to process order: " + ex.getCause().getMessage());
                    return ResponseEntity.internalServerError().body(errorResponse);
                });
    }
}
