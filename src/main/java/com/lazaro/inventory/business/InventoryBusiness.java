package com.lazaro.inventory.business;

import com.lazaro.inventory.inventory.Inventory;
import com.lazaro.inventory.inventory.InventoryDTO;
import com.lazaro.inventory.inventory.InventoryRepository;
import com.lazaro.inventory.util.InventoryConverter;
import com.lazaro.inventory.infra.exception.BusinessException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class InventoryBusiness {

  private final InventoryRepository inventoryRepository;

  public InventoryBusiness(InventoryRepository inventoryRepository){
    this.inventoryRepository = inventoryRepository;
  }

  @Transactional
  public InventoryDTO create(@Valid InventoryDTO inventoryDTO) {
    inventoryRepository.findByProductIdAndLocationId(inventoryDTO.productId(), inventoryDTO.locationId())
        .ifPresent(existing -> {
          throw new BusinessException("Já existe um registro de inventário para este produto nesta localização.");
        });

    Inventory inventory = InventoryConverter.convert(inventoryDTO);
    Inventory saved = inventoryRepository.save(inventory);
    return InventoryConverter.convert(saved);
  }

  public InventoryDTO findById(Long id) {
    Inventory inventory = inventoryRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Registro de inventário não encontrado com id: " + id));
    return InventoryConverter.convert(inventory);
  }

  public List<InventoryDTO> findByProductId(UUID productId) {
    return inventoryRepository.findByProductId(productId)
        .stream()
        .map(InventoryConverter::convert)
        .toList();
  }

  public List<InventoryDTO> findAll() {
    List<Inventory> inventories = inventoryRepository.findAll();

    return inventories.stream()
        .map(InventoryConverter::convert)
        .toList();
  }

}
