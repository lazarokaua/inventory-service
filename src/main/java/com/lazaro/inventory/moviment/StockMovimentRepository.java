package com.lazaro.inventory.moviment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository 
public interface StockMovimentRepository extends JpaRepository<StockMoviment, Long>{

}
