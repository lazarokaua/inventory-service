package com.lazaro.inventory.business;

import com.lazaro.inventory.inventory.Inventory;
import com.lazaro.inventory.inventory.InventoryDTO;
import com.lazaro.inventory.inventory.InventoryRepository;
import com.lazaro.inventory.util.InventoryConverter;
import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

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

  public InventoryDTO findById(Long id) {
    return InventoryConverter.convert(inventoryRepository.findById(id).get());
  }

  public List<InventoryDTO> findAll() {
    List<Inventory> inventories = inventoryRepository.findAll();

    return inventories.stream()
    .map(InventoryConverter::convert)
    .collect(Collectors.toList());

    
  }

}
