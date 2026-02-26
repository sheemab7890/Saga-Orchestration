package org.example.events;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Published by Payment Service when payment fails.
 * Order Service listens to cancel order.
 * Inventory Service listens to release reserved stock (compensating transaction).
 */


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentFailedEvent {

    private String orderId;
    private String customerId;
    private String productId;
    private int quantity;
    private String reason;
    private LocalDateTime failedAt;


    public PaymentFailedEvent(String orderId, String customerId,
                              String productId, int quantity, String reason) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.productId = productId;
        this.quantity = quantity;
        this.reason = reason;
        this.failedAt = LocalDateTime.now();
    }

}
