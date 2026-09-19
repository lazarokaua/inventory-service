package com.lazaro.inventory.moviment;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * StockMoviment
 */

@Entity 
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@EqualsAndHashCode(callSuper = false)
public class StockMoviment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMoviment;
    @Column 
    private UUID productId;
    @Column 
    private UUID locationId;
    @Column 
    @Enumerated(EnumType.STRING)
    private MovimentType movimentType;
    @Column 
    private Integer quantity;
    @Column 
    private Integer previousQuantity;
    @Column 
    private Integer newQuantity;
    @Column 
    private String reason;
    @Column 
    private LocalDate createdAt;
}
