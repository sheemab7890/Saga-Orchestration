package com.sheemab.order_service.entity.enums;



/**
 * Represents all possible states of an Order in the Saga lifecycle.
 *
 * Flow (Happy Path):
 *   PENDING -> INVENTORY_RESERVED -> CONFIRMED
 *
 * Flow (Compensation):
 *   PENDING -> CANCELLED (if inventory fails)
 *   PENDING -> INVENTORY_RESERVED -> CANCELLED (if payment fails)
 */
public enum OrderStatus {
    PENDING,
    INVENTORY_RESERVED,
    CONFIRMED,
    CANCELLED
}
