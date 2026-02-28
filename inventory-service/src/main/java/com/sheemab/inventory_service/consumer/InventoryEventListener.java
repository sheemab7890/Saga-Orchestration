package com.sheemab.inventory_service.consumer;


import com.sheemab.inventory_service.service.InventoryService;
import com.sheemab.inventory_service.topics.KafkaTopics;
import lombok.RequiredArgsConstructor;
import org.example.events.OrderCreatedEvent;
import org.example.events.PaymentFailedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Inventory Service event listeners.
 * Reacts to Order and Payment events as part of the choreography saga.
 */
@Component
@RequiredArgsConstructor
public class InventoryEventListener {

    private static final Logger log = LoggerFactory.getLogger(InventoryEventListener.class);

    private final InventoryService inventoryService;


    /**
     * Listen to ORDER_CREATED → try to reserve inventory
     */
    @KafkaListener(topics = KafkaTopics.ORDER_CREATED,
            groupId = "inventory-service-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("Received OrderCreatedEvent for orderId={}", event.getOrderId());
        inventoryService.reserveInventory(event);
    }

    /**
     * Listen to PAYMENT_FAILED → release reserved inventory (compensating transaction)
     */
    @KafkaListener(topics = KafkaTopics.PAYMENT_FAILED,
            groupId = "inventory-service-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void onPaymentFailed(PaymentFailedEvent event) {
        log.warn("Received PaymentFailedEvent for orderId={}. Releasing inventory.", event.getOrderId());
        inventoryService.releaseInventory(
                event.getProductId(),
                event.getQuantity(),
                event.getOrderId()
        );
    }
}
