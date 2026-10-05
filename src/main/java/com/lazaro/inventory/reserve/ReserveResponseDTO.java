package com.lazaro.inventory.reserve;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReserveResponseDTO(
        Long idReserva,
        UUID productId,
        UUID locationId,
        Integer quantity,
        ReserveStatus status,
        LocalDateTime reservedAt,
        LocalDateTime expiresAt,
        UUID orderId
) {
}
