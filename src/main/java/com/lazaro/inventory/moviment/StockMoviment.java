package com.lazaro.inventory.moviment;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * StockMoviment
 */

@Entity 
@Table(name = "stock_moviment")
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@EqualsAndHashCode(callSuper = false)
public class StockMoviment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMoviment;

    @Column(nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private UUID locationId;

    @Column
    private UUID destinationLocationId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MovimentType movimentType;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer previousQuantity;

    @Column(nullable = false)
    private Integer newQuantity;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
