package com.lazaro.inventory.reserve;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reserves")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reserve {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long idReserva;

  private UUID productId;
  private UUID locationId;
  private Integer quantity;

  @Enumerated(EnumType.STRING)
  private ReserveStatus status;

  private LocalDateTime reservedAt;
  private LocalDateTime expiresAt;
  private UUID orderId;
}
