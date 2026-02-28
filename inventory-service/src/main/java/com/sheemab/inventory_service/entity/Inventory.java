package com.sheemab.inventory_service.entity;



import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inventory")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Inventory {

    @Id
    private String productId;

    @Column(nullable = false)
    private int availableQuantity;

    @Column(nullable = false)
    private int reservedQuantity;


    public Inventory(String productId, int availableQuantity) {
        this.productId = productId;
        this.availableQuantity = availableQuantity;
        this.reservedQuantity = 0;
    }

    public boolean canReserve(int quantity) {
        return availableQuantity >= quantity;
    }

    public void reserve(int quantity) {
        this.availableQuantity -= quantity;
        this.reservedQuantity += quantity;
    }

    public void release(int quantity) {
        this.availableQuantity += quantity;
        this.reservedQuantity -= quantity;
    }

}
