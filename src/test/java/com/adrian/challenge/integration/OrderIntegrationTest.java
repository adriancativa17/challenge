package com.adrian.challenge.integration;

import com.adrian.challenge.models.Item;
import com.adrian.challenge.models.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void shouldProcessOrderSuccessfully() throws Exception {
        // Given
        List<Item> items = List.of(
                Item.builder().productId(1L).name("item1").quantity(1).unitPrice(new BigDecimal("100.0")).build(),
                Item.builder().productId(2L).name("item2").quantity(1).unitPrice(new BigDecimal("100.0")).build()
        );
        Order order = Order.builder()
                .customer("Test Customer")
                .items(items)
                .amount(new BigDecimal("200.00"))
                .build();

        // When
        mockMvc.perform(post("/processOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(order)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void shouldReturnErrorWhenOrderIsInvalid() throws Exception {
        // Given
        Order order = Order.builder()
                .customer("") // Empty customer
                .items(null) // Null items
                .amount(new BigDecimal("-200.00")) // Invalid amount
                .build();

        // When
        mockMvc.perform(post("/processOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(order)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    void shouldHandleConcurrentRequests() throws Exception {
        // Given
        int numberOfRequests = 5;
        List<CompletableFuture<Void>> futures = new java.util.ArrayList<>();

        // When
        for (int i = 0; i < numberOfRequests; i++) {
            Order order = createTestOrder("Customer " + i, BigDecimal.valueOf(100.0 * (i + 1)), List.of(
                    Item.builder().productId((long) i).name("item"+i).quantity(1).unitPrice(new BigDecimal("100.0")).build()));
            
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    mockMvc.perform(post("/processOrder")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(mapper.writeValueAsString(order)))
                            .andExpect(status().is2xxSuccessful());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            futures.add(future);
        }

        // Wait for all requests to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        
        // Then
        await().atMost(10, TimeUnit.SECONDS)
               .until(() -> futures.stream().allMatch(CompletableFuture::isDone));
    }
    
    private Order createTestOrder(String customer, BigDecimal amount, List<Item> items) {
        return Order.builder()
                .customer(customer)
                .items(items)
                .amount(amount)
                .build();
    }
}
