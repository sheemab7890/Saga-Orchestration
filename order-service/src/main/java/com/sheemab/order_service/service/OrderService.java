package com.sheemab.order_service.service;




import com.sheemab.order_service.entity.Order;
import com.sheemab.order_service.entity.enums.OrderStatus;
import com.sheemab.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.example.events.OrderCreatedEvent;
import org.example.topics.KafkaTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;


    /**
     * STEP 1 of Saga: Create order in PENDING state and publish OrderCreatedEvent.
     * The event kicks off the saga.
     */
    @Transactional
    public Order createOrder(String customerId, String productId,
                             int quantity, BigDecimal totalAmount) {
        // Save order with PENDING status
        String orderId = UUID.randomUUID().toString();
        Order order = new Order(orderId, customerId, productId, quantity, totalAmount);
        orderRepository.save(order);

        log.info("Order created with id={}, status=PENDING", orderId);

        // Publish event to start the saga
        OrderCreatedEvent event = new OrderCreatedEvent(orderId, customerId,
                productId, quantity, totalAmount);
        kafkaTemplate.send(KafkaTopics.ORDER_CREATED, orderId, event);

        log.info("Published OrderCreatedEvent for orderId={}", orderId);
        return order;
    }

    /**
     * Called when Inventory Service successfully reserves stock.
     * Updates order to INVENTORY_RESERVED.
     */
    @Transactional
    public void markInventoryReserved(String orderId) {
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setStatus(OrderStatus.INVENTORY_RESERVED);
            orderRepository.save(order);
            log.info("Order {} updated to INVENTORY_RESERVED", orderId);
        });
    }

    /**
     * Called when Payment Service successfully processes payment.
     * Updates order to CONFIRMED. Saga completes successfully.
     */
    @Transactional
    public void confirmOrder(String orderId) {
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setStatus(OrderStatus.CONFIRMED);
            orderRepository.save(order);
            log.info("Order {} CONFIRMED - Saga completed successfully!", orderId);
        });
    }

    /**
     * Compensating transaction: Cancel order with a reason.
     * Called when any step in the saga fails.
     */
    @Transactional
    public void cancelOrder(String orderId, String reason) {
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setStatus(OrderStatus.CANCELLED);
            order.setFailureReason(reason);
            orderRepository.save(order);
            log.warn("Order {} CANCELLED. Reason: {}", orderId, reason);
        });
    }

    public Order getOrder(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
    }
}
