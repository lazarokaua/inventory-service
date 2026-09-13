package com.lazaro.inventory.inventory;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity(name = "inventory")
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@EqualsAndHashCode(callSuper = false)
public class Inventory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private UUID productId;
    @Column
    private UUID locationId;
    @Column
    private Integer quantity;
    @Column
    private Integer reservedQuantity;
    @Column
    private LocalDate createdAt;
    @Column
    private LocalDate updatedAt;
    

}
