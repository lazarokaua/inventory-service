package com.lazaro.inventory.moviment;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * StockMovimentDTO
 */
public record StockMovimentDTO(
    @NotNull 
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
    String reason
) 
{}
