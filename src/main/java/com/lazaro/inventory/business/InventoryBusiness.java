package com.lazaro.inventory.business;

import com.lazaro.inventory.inventory.InventoryDTO;
import com.lazaro.inventory.inventory.InventoryRepository;
import com.lazaro.inventory.util.InventoryConverter;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public class InventoryBusiness {

  private final InventoryRepository inventoryRepository;

  public InventoryBusiness(InventoryRepository inventoryRepository){
    this.inventoryRepository = inventoryRepository;
  }

  public InventoryDTO create(@Valid InventoryDTO inventoryDTO) {
    return InventoryConverter.convert(
      inventoryRepository.save(
            InventoryConverter.convert(inventoryDTO)
    )
    );
    
  }
}
