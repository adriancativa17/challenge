package com.adrian.challenge.validators;

import com.adrian.challenge.exceptions.InvalidOrderAmountException;
import com.adrian.challenge.models.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OrderValidator {
    private static final BigDecimal TOLERANCE = new BigDecimal("0.01");

    public void validate(Order order) {
        validateTotalAmount(order);
    }

    private void validateTotalAmount(Order order) {
        BigDecimal expectedAmount = order.getItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal difference = expectedAmount.subtract(order.getAmount()).abs();

        if (difference.compareTo(TOLERANCE) > 0) {
            throw new InvalidOrderAmountException(
                    "Mismatch between orderAmount and sum of item totals. Expected: " +
                            expectedAmount + ", but got: " + order.getAmount()
            );
        }
    }
}
