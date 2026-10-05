package com.lazaro.inventory.util;

import com.lazaro.inventory.reserve.Reserve;
import com.lazaro.inventory.reserve.ReserveResponseDTO;

public class ReserveConverter {

    public static ReserveResponseDTO convert(Reserve reserve) {
        if (reserve == null) {
            return null;
        }
        return new ReserveResponseDTO(
                reserve.getIdReserva(),
                reserve.getProductId(),
                reserve.getLocationId(),
                reserve.getQuantity(),
                reserve.getStatus(),
                reserve.getReservedAt(),
                reserve.getExpiresAt(),
                reserve.getOrderId()
        );
    }
}
