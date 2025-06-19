package com.adrian.challenge.unit;

import com.adrian.challenge.exceptions.InvalidOrderAmountException;
import com.adrian.challenge.models.Item;
import com.adrian.challenge.models.Order;
import com.adrian.challenge.services.OrderService;
import com.adrian.challenge.validators.OrderValidator;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    private MeterRegistry meterRegistry;

    @Mock
    private OrderValidator validator;

    @InjectMocks
    private OrderService service;

    private Order testOrder;
    private List<Item> testItems;

    @BeforeEach
    void setup() {
        testItems = List.of(
                Item.builder()
                        .productId(1L)
                        .name("Test Item")
                        .quantity(2)
                        .unitPrice(new BigDecimal("50.00"))
                        .build()
        );

        testOrder = Order.builder()
                .customer("Test Customer")
                .items(testItems)
                .amount(new BigDecimal("100.00"))
                .build();

        meterRegistry = new SimpleMeterRegistry();

        service = new OrderService(meterRegistry, validator);
    }

    @Test
    void processOrder_shouldReturnSuccessMessage() throws Exception {
        // Given
        doNothing().when(validator).validate(any(Order.class));

        // When
        CompletableFuture<String> future = service.processOrder(testOrder);

        // Then
        String result = future.get(1, TimeUnit.SECONDS);
        assertTrue(result.contains("processed successfully"));
        verify(validator).validate(testOrder);
    }

    @Test
    void processOrder_whenValidatorThrowsException_shouldPropagateError() {
        // Given
        String errorMessage = "Invalid order amount";
        doThrow(new InvalidOrderAmountException(errorMessage))
                .when(validator).validate(any(Order.class));

        // When & Then
        CompletionException exception = assertThrows(CompletionException.class, 
                () -> service.processOrder(testOrder).join());
        
        assertEquals(errorMessage, exception.getCause().getMessage());
        verify(validator).validate(testOrder);
    }

    @Test
    void processOrder_whenProcessingFails_shouldReturnErrorResponse() {
        // Given
        doThrow(new RuntimeException("Processing failed"))
                .when(validator).validate(any(Order.class));

        // When
        CompletableFuture<String> future = service.processOrder(testOrder);

        // Then
        CompletionException exception = assertThrows(CompletionException.class, future::join);
        assertTrue(exception.getCause() instanceof RuntimeException);
        assertEquals("Processing failed", exception.getCause().getMessage());
    }

    @Test
    void processOrder_shouldHandleConcurrentRequests() throws Exception {
        // Given
        int numberOfRequests = 5;
        doNothing().when(validator).validate(any(Order.class));
        // Create a list of futures
        List<CompletableFuture<String>> futures = new ArrayList<>();

        // When
        for (int i = 0; i < numberOfRequests; i++) {
            Order order = Order.builder()
                    .customer("Customer " + i)
                    .items(testItems)
                    .amount(new BigDecimal("100.00"))
                    .build();
            futures.add(service.processOrder(order));
        }

        // Wait for all requests to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(5, TimeUnit.SECONDS);

        // Then
        verify(validator, times(numberOfRequests)).validate(any(Order.class));
    }
}
