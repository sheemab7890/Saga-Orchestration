package com.sheemab.order_service.consumer;


import com.sheemab.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.example.events.InventoryReservationFailedEvent;
import org.example.events.InventoryReservedEvent;
import org.example.events.PaymentFailedEvent;
import org.example.events.PaymentProcessedEvent;
import com.sheemab.order_service.topics.KafkaTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Order Service listens to reply events from Inventory and Payment services.
 * This is the "choreography" — no central orchestrator, just reactive listeners.
 */
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);

    private final OrderService orderService;

    /**
     * Inventory reserved successfully → update order status
     */
    @KafkaListener(topics = KafkaTopics.INVENTORY_RESERVED,
            groupId = "order-service-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void onInventoryReserved(InventoryReservedEvent event) {
        log.info("Received InventoryReservedEvent for orderId={}", event.getOrderId());
        orderService.markInventoryReserved(event.getOrderId());
    }

    /**
     * Inventory reservation failed → compensate by cancelling order
     */
    @KafkaListener(topics = KafkaTopics.INVENTORY_RESERVATION_FAILED,
            groupId = "order-service-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void onInventoryReservationFailed(InventoryReservationFailedEvent event) {
        log.warn("Received InventoryReservationFailedEvent for orderId={}", event.getOrderId());
        orderService.cancelOrder(event.getOrderId(), event.getReason());
    }

    /**
     * Payment processed successfully → confirm order. Saga done!
     */
    @KafkaListener(topics = KafkaTopics.PAYMENT_PROCESSED,
            groupId = "order-service-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void onPaymentProcessed(PaymentProcessedEvent event) {
        log.info("Received PaymentProcessedEvent for orderId={}", event.getOrderId());
        orderService.confirmOrder(event.getOrderId());
    }

    /**
     * Payment failed → compensate by cancelling order
     * (Inventory service will also listen and release reserved stock)
     */
    @KafkaListener(topics = KafkaTopics.PAYMENT_FAILED,
            groupId = "order-service-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void onPaymentFailed(PaymentFailedEvent event) {
        log.warn("Received PaymentFailedEvent for orderId={}", event.getOrderId());
        orderService.cancelOrder(event.getOrderId(), event.getReason());
    }
}
