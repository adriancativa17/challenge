package com.adrian.challenge.services;

import com.adrian.challenge.models.Order;
import com.adrian.challenge.validators.OrderValidator;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class OrderService {
    private final Map<Long, Order> processedOrders = new ConcurrentHashMap<>();
    private final ExecutorService executorService;

    private final MeterRegistry meterRegistry;

    private final OrderValidator validator;

    public OrderService(MeterRegistry meterRegistry, OrderValidator validator) {
        this.meterRegistry = meterRegistry;
        this.validator = validator;
        this.executorService = new ThreadPoolExecutor(
                100, // core threads
                300, // max threads
                30L, TimeUnit.SECONDS, // keep alive time
                new LinkedBlockingQueue<>(500) // work queue
        );
    }

    public CompletableFuture<String> processOrder(Order order) {
        Timer.Sample sample = Timer.start(meterRegistry);

        return CompletableFuture.supplyAsync(() -> {
            try {
                validator.validate(order);
                simulateBusinessLogic(order); // Simulate business logic
                processedOrders.put(order.getId(), order); // Simulate order processing
                return "Order " + order.getId() + " processed successfully";
            } catch (Exception e) {
                throw new CompletionException(e.getMessage(), e);
            }
            finally {
                sample.stop(
                        Timer.builder("order.processing.duration") // Metric name
                                .description("Tiempo de procesamiento de pedidos") // Metric description
                                .tag("orderId", String.valueOf(order.getId())) // Metric tag
                                .register(meterRegistry) // Register the metric
                );
            }
        }, executorService);
    }

    private void simulateBusinessLogic(Order order) {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(100, 501)); // Simulate processing time
            order.setId(ThreadLocalRandom.current().nextLong(1_000L, 9_999L)); // Simulate ID generation
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Processing was interrupted", e);
        }
    }

    public List<Order> getProcessedOrders() {
        return List.copyOf(processedOrders.values());
    }
}
