package com.lazaro.inventory.inventory;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository 
public interface InventoryRepository extends JpaRepository<Inventory, Long>{

    @Query("SELECT i FROM Inventory i WHERE i.productId = :productId")
    List<Inventory> findByProductId(UUID productId);

}
