package org.example.events;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Published by Inventory Service when stock is successfully reserved.
 * Payment Service listens to this event.
 */

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryReservedEvent {

    private String orderId;
    private String customerId;
    private String productId;
    private int quantity;
    private BigDecimal totalAmount;
    private LocalDateTime reservedAt;


    public InventoryReservedEvent(String orderId, String customerId, String productId,
                                  int quantity, BigDecimal totalAmount) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.reservedAt = LocalDateTime.now();
    }

}
