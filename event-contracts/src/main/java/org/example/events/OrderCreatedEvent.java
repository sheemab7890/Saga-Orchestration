package org.example.events;


import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Published by Order Service when a new order is created.
 * Inventory Service listens to this event.
 */

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreatedEvent {

    private String orderId;
    private String customerId;
    private String productId;
    private int quantity;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;


    public OrderCreatedEvent(String orderId, String customerId, String productId,
                             int quantity, BigDecimal totalAmount) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.createdAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "OrderCreatedEvent{orderId='" + orderId + "', productId='" + productId + "', quantity=" + quantity + "}";
    }
}
