package com.lazaro.inventory.inventory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long>{

    @Query("SELECT i FROM Inventory i WHERE i.productId = :productId")
    List<Inventory> findByProductId(UUID productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Inventory> findByProductIdAndLocationId(UUID productId, UUID locationId);

}
