package com.lazaro.inventory.reserve;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ReserveDTO(
        @NotNull(message = "O productId é obrigatório")
        UUID productId,

        @NotNull(message = "O locationId é obrigatório")
        UUID locationId,

        @NotNull(message = "A quantidade é obrigatória")
        @Min(value = 1, message = "A quantidade deve ser maior que zero")
        Integer quantity,

        @NotNull(message = "O orderId é obrigatório")
        UUID orderId
) {
}