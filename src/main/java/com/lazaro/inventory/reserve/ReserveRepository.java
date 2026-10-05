package com.lazaro.inventory.reserve;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReserveRepository extends JpaRepository<Reserve, Long> {

  Optional<Reserve> findByOrderId(UUID orderId);

}