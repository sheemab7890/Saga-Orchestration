package com.sheemab.inventory_service.service;


import com.sheemab.inventory_service.entity.Inventory;
import com.sheemab.inventory_service.repository.InventoryRepository;
import com.sheemab.inventory_service.topics.KafkaTopics;
import lombok.RequiredArgsConstructor;
import org.example.events.InventoryReservationFailedEvent;
import org.example.events.InventoryReservedEvent;
import org.example.events.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    private final InventoryRepository inventoryRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * STEP 2 of Saga: Reserve inventory.
     * On success  → publish InventoryReservedEvent (triggers Payment)
     * On failure → publish InventoryReservationFailedEvent (triggers Order cancellation)
     */
    @Transactional
    public void reserveInventory(OrderCreatedEvent event) {
        log.info("Attempting to reserve {} units of product {} for order {}",
                event.getQuantity(), event.getProductId(), event.getOrderId());

        Inventory inventory = inventoryRepository.findById(event.getProductId())
                .orElse(null);

        if (inventory == null || !inventory.canReserve(event.getQuantity())) {
            String reason = inventory == null
                    ? "Product not found: " + event.getProductId()
                    : "Insufficient stock. Available: " + inventory.getAvailableQuantity()
                    + ", Required: " + event.getQuantity();

            log.warn("Inventory reservation FAILED for orderId={}: {}", event.getOrderId(), reason);

            // Publish failure event → Order Service will cancel the order
            InventoryReservationFailedEvent failedEvent =
                    new InventoryReservationFailedEvent(event.getOrderId(), reason);
            kafkaTemplate.send(KafkaTopics.INVENTORY_RESERVATION_FAILED, event.getOrderId(), failedEvent);
            return;
        }

        // Reserve the stock
        inventory.reserve(event.getQuantity());
        inventoryRepository.save(inventory);

        log.info("Inventory reserved for orderId={}. Remaining stock: {}",
                event.getOrderId(), inventory.getAvailableQuantity());

        // Publish success event → Payment Service will process payment
        InventoryReservedEvent reservedEvent = new InventoryReservedEvent(
                event.getOrderId(), event.getCustomerId(),
                event.getProductId(), event.getQuantity(), event.getTotalAmount()
        );
        kafkaTemplate.send(KafkaTopics.INVENTORY_RESERVED, event.getOrderId(), reservedEvent);
    }

    /**
     * Compensating transaction: Release reserved inventory when payment fails.
     */
    @Transactional
    public void releaseInventory(String productId, int quantity, String orderId) {
        inventoryRepository.findById(productId).ifPresent(inventory -> {
            inventory.release(quantity);
            inventoryRepository.save(inventory);
            log.info("Released {} units of product {} for failed order {}",
                    quantity, productId, orderId);
        });
    }

    // For seeding initial inventory data
    @Transactional
    public Inventory addInventory(String productId, int quantity) {
        Inventory inventory = new Inventory(productId, quantity);
        return inventoryRepository.save(inventory);
    }

    public Inventory getInventory(String productId) {
        return inventoryRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found: " + productId));
    }
}
