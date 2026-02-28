package com.sheemab.inventory_service.topics;


import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class KafkaTopics {

    public static final String ORDER_CREATED = "order-created";
    public static final String INVENTORY_RESERVED = "inventory-reserved";
    public static final String INVENTORY_RESERVATION_FAILED = "inventory-reservation-failed";
    public static final String PAYMENT_FAILED = "payment-failed";
}
