package com.adrian.challenge.models;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Data
public class Order {
    private Long id;

    @NotBlank(message = "Customer ID is required")
    private String customer;

    @Min(value = 0, message = "Order amount must be non-negative")
    private BigDecimal amount;

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<Item> items;
}
