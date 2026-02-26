package org.example.events;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Published by Inventory Service when stock reservation fails.
 * Order Service listens to this to cancel the order (compensating transaction).
 */

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryReservationFailedEvent {

    private String orderId;
    private String reason;
    private LocalDateTime failedAt;

    public InventoryReservationFailedEvent(String orderId, String reason) {
        this.orderId = orderId;
        this.reason = reason;
        this.failedAt = LocalDateTime.now();
    }

}
