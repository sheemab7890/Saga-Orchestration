package com.sheemab.inventory_service.controller;


import com.sheemab.inventory_service.entity.Inventory;
import com.sheemab.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;


    @PostMapping
    public ResponseEntity<Inventory> addInventory(@RequestBody AddInventoryRequest request) {
        return ResponseEntity.ok(inventoryService.addInventory(request.productId(), request.quantity()));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Inventory> getInventory(@PathVariable String productId) {
        return ResponseEntity.ok(inventoryService.getInventory(productId));
    }

    public record AddInventoryRequest(String productId, int quantity) {}
}
