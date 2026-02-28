package com.sheemab.order_service.topics;


/**
 * Central place to define all Kafka topic names.
 * Avoids magic strings scattered across the codebase.
 */
public final class KafkaTopics {

    private KafkaTopics() {}

    // Order Service publishes
    public static final String ORDER_CREATED = "order-created";

    // Inventory Service publishes
    public static final String INVENTORY_RESERVED = "inventory-reserved";
    public static final String INVENTORY_RESERVATION_FAILED = "inventory-reservation-failed";

    // Payment Service publishes
    public static final String PAYMENT_PROCESSED = "payment-processed";
    public static final String PAYMENT_FAILED = "payment-failed";
}
