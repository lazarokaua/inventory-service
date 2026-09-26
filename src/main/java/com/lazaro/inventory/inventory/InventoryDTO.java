package com.lazaro.inventory.inventory;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record InventoryDTO(
    Long id,
    @NotNull 
    UUID productId,
    @NotNull
    UUID locationId,
    @NotNull @Min(value = 0, message = "Quantity cannot be negative")
    Integer quantity,
    Integer reservedQuantity
) {
    public InventoryDTO(UUID productId, UUID locationId, Integer quantity) {
        this(null, productId, locationId, quantity, 0);
    }
}
