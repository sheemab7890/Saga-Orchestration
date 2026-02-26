package org.example.events;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Published by Payment Service when payment is successfully processed.
 * Order Service listens to this to mark order as CONFIRMED.
 */


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentProcessedEvent {

    private String orderId;
    private String customerId;
    private BigDecimal amount;
    private String transactionId;
    private LocalDateTime processedAt;


    public PaymentProcessedEvent(String orderId, String customerId,
                                 BigDecimal amount, String transactionId) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
        this.transactionId = transactionId;
        this.processedAt = LocalDateTime.now();
    }

}
