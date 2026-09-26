package com.lazaro.inventory.moviment;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * StockMovimentDTO
 */
public record StockMovimentDTO(
    Long idMoviment,
    @NotNull
    UUID productId,
    @NotNull
    UUID locationId,
    @NotNull
    MovimentType movimentType,
    @NotNull @Min(value = 0, message = "Quantity cannot be negative")
    Integer quantity,
    @NotNull
    String reason,
    Integer previousQuantity,
    Integer newQuantity,
    LocalDateTime createdAt
) {
    public StockMovimentDTO(Long idMoviment, UUID productId, UUID locationId, MovimentType movimentType, Integer quantity, String reason) {
        this(idMoviment, productId, locationId, movimentType, quantity, reason, null, null, null);
    }
}
