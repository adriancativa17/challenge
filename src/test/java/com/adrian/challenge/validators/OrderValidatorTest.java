package com.adrian.challenge.validators;

import com.adrian.challenge.exceptions.InvalidOrderAmountException;
import com.adrian.challenge.models.Item;
import com.adrian.challenge.models.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class OrderValidatorTest {

    private OrderValidator validator;

    @BeforeEach
    void setUp() {
        validator = new OrderValidator();
    }

    @Test
    void validate_WithValidOrder_ShouldNotThrowException() {
        // Given
        Order order = createValidOrder();

        // Then
        assertDoesNotThrow(() -> validator.validate(order));
    }

    @Test
    void validate_WithNullOrder_ShouldThrowException() {
        // Then
        assertThrows(NullPointerException.class, () -> validator.validate(null));
    }

    @Test
    void validate_WithNullItems_ShouldThrowException() {
        // Given
        Order order = createValidOrder();
        order.setItems(null);

        // Then
        assertThrows(NullPointerException.class, () -> validator.validate(order));
    }

    @Test
    void validate_WithEmptyItems_ShouldThrowException() {
        // Given
        Order order = createValidOrder();
        order.setItems(Collections.emptyList());

        // Then
        assertThrows(InvalidOrderAmountException.class, () -> validator.validate(order));
    }

    @Test
    void validate_WithAmountWithinTolerance_ShouldNotThrowException() {
        // Given
        Order order = createValidOrder();
        order.setAmount(new BigDecimal("100.01"));

        // Then
        assertDoesNotThrow(() -> validator.validate(order));
    }

    @Test
    void validate_WithMultipleItems_ShouldCalculateTotalCorrectly() {
        // Given
        List<Item> items = Arrays.asList(
            Item.builder()
                .productId(1L)
                .name("Item 1")
                .quantity(2)
                .unitPrice(new BigDecimal("10.50"))
                .build(),
            Item.builder()
                .productId(2L)
                .name("Item 2")
                .quantity(1)
                .unitPrice(new BigDecimal("79.00"))
                .build()
        );
        
        Order order = Order.builder()
            .customer("Test Customer")
            .items(items)
            .amount(new BigDecimal("100.00")) // 10.50*2 + 79.00 = 100.00
            .build();

        // Then
        assertDoesNotThrow(() -> validator.validate(order));
    }

    private Order createValidOrder() {
        Item item = Item.builder()
            .productId(1L)
            .name("Test Item")
            .quantity(2)
            .unitPrice(new BigDecimal("50.00"))
            .build();

        return Order.builder()
            .customer("Test Customer")
            .items(Collections.singletonList(item))
            .amount(new BigDecimal("100.00")) // 50.00 * 2 = 100.00
            .build();
    }
}
