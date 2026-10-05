package com.lazaro.inventory.business;

import com.lazaro.inventory.infra.exception.BusinessException;
import com.lazaro.inventory.inventory.Inventory;
import com.lazaro.inventory.inventory.InventoryRepository;
import com.lazaro.inventory.reserve.Reserve;
import com.lazaro.inventory.reserve.ReserveDTO;
import com.lazaro.inventory.reserve.ReserveRepository;
import com.lazaro.inventory.reserve.ReserveResponseDTO;
import com.lazaro.inventory.reserve.ReserveStatus;
import com.lazaro.inventory.util.ReserveConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ReserveBusiness {

  private final ReserveRepository reserveRepository;
  private final InventoryRepository inventoryRepository;

  public ReserveBusiness(ReserveRepository reserveRepository, InventoryRepository inventoryRepository) {
    this.reserveRepository = reserveRepository;
    this.inventoryRepository = inventoryRepository;
  }

  @Transactional
  public ReserveResponseDTO createReserve(ReserveDTO reserveDTO) {
    reserveRepository.findByOrderId(reserveDTO.orderId())
            .filter(r -> r.getStatus() == ReserveStatus.PENDING)
            .ifPresent(r -> {
              throw new BusinessException("Já existe uma reserva pendente para o pedido: " + reserveDTO.orderId());
            });

    Inventory inventory = inventoryRepository.findByProductIdAndLocationId(reserveDTO.productId(), reserveDTO.locationId())
            .orElseThrow(() -> new BusinessException("Inventário não encontrado para o produto e localização fornecidos."));

    int physicalQuantity = inventory.getQuantity() != null ? inventory.getQuantity() : 0;
    int reservedQuantity = inventory.getReservedQuantity() != null ? inventory.getReservedQuantity() : 0;
    int availableQuantity = physicalQuantity - reservedQuantity;

    if (availableQuantity < reserveDTO.quantity()) {
      throw new BusinessException("Quantidade disponível insuficiente para reserva. Disponível: " + availableQuantity);
    }

    inventory.setReservedQuantity(reservedQuantity + reserveDTO.quantity());
    inventoryRepository.save(inventory);

    Reserve reserve = Reserve.builder()
            .productId(reserveDTO.productId())
            .locationId(reserveDTO.locationId())
            .quantity(reserveDTO.quantity())
            .status(ReserveStatus.PENDING)
            .reservedAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plusHours(24))
            .orderId(reserveDTO.orderId())
            .build();

    Reserve savedReserve = reserveRepository.save(reserve);
    return ReserveConverter.convert(savedReserve);
  }

  @Transactional
  public ReserveResponseDTO confirmReserve(UUID orderId) {
    Reserve reserve = reserveRepository.findByOrderId(orderId)
            .orElseThrow(() -> new BusinessException("Reserva não encontrada para o pedido informado."));

    if (reserve.getStatus() != ReserveStatus.PENDING) {
      throw new BusinessException("Apenas reservas com status PENDING podem ser confirmadas. Status atual: " + reserve.getStatus());
    }

    Inventory inventory = inventoryRepository.findByProductIdAndLocationId(reserve.getProductId(), reserve.getLocationId())
            .orElseThrow(() -> new BusinessException("Inventário não encontrado para o produto e localização fornecidos."));

    int physicalQuantity = inventory.getQuantity() != null ? inventory.getQuantity() : 0;
    int reservedQuantity = inventory.getReservedQuantity() != null ? inventory.getReservedQuantity() : 0;

    if (reserve.getExpiresAt() != null && LocalDateTime.now().isAfter(reserve.getExpiresAt())) {
      reserve.setStatus(ReserveStatus.EXPIRED);
      inventory.setReservedQuantity(Math.max(0, reservedQuantity - reserve.getQuantity()));
      inventoryRepository.save(inventory);
      reserveRepository.save(reserve);
      throw new BusinessException("A reserva para o pedido expirou e não pode ser confirmada.");
    }

    inventory.setQuantity(Math.max(0, physicalQuantity - reserve.getQuantity()));
    inventory.setReservedQuantity(Math.max(0, reservedQuantity - reserve.getQuantity()));
    inventoryRepository.save(inventory);

    reserve.setStatus(ReserveStatus.CONFIRMED);
    Reserve savedReserve = reserveRepository.save(reserve);

    return ReserveConverter.convert(savedReserve);
  }

  @Transactional
  public ReserveResponseDTO cancelReserve(UUID orderId) {
    Reserve reserve = reserveRepository.findByOrderId(orderId)
            .orElseThrow(() -> new BusinessException("Reserva não encontrada para o pedido informado."));

    if (reserve.getStatus() != ReserveStatus.PENDING) {
      throw new BusinessException("Apenas reservas com status PENDING podem ser canceladas. Status atual: " + reserve.getStatus());
    }

    Inventory inventory = inventoryRepository.findByProductIdAndLocationId(reserve.getProductId(), reserve.getLocationId())
            .orElseThrow(() -> new BusinessException("Inventário não encontrado para o produto e localização fornecidos."));

    int reservedQuantity = inventory.getReservedQuantity() != null ? inventory.getReservedQuantity() : 0;
    inventory.setReservedQuantity(Math.max(0, reservedQuantity - reserve.getQuantity()));
    inventoryRepository.save(inventory);

    reserve.setStatus(ReserveStatus.CANCELED);
    Reserve savedReserve = reserveRepository.save(reserve);

    return ReserveConverter.convert(savedReserve);
  }

  public ReserveResponseDTO findByOrderId(UUID orderId) {
    Reserve reserve = reserveRepository.findByOrderId(orderId)
            .orElseThrow(() -> new BusinessException("Reserva não encontrada para o pedido informado."));
    return ReserveConverter.convert(reserve);
  }

  public ReserveResponseDTO findById(Long idReserva) {
    Reserve reserve = reserveRepository.findById(idReserva)
            .orElseThrow(() -> new BusinessException("Reserva não encontrada com id: " + idReserva));
    return ReserveConverter.convert(reserve);
  }
}
