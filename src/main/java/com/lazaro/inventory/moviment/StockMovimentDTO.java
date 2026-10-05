package com.lazaro.inventory.moviment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record StockMovimentDTO(
        Long idMoviment,
        @NotNull
        UUID productId,
        @NotNull
        UUID locationId,
        UUID destinationLocationId,
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
    this(idMoviment, productId, locationId, null, movimentType, quantity, reason, null, null, null);
  }

  public StockMovimentDTO(Long idMoviment, UUID productId, UUID locationId, UUID destinationLocationId, MovimentType movimentType, Integer quantity, String reason) {
    this(idMoviment, productId, locationId, destinationLocationId, movimentType, quantity, reason, null, null, null);
  }
}
