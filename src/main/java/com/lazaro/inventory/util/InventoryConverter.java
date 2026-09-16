package com.lazaro.inventory.util;


import java.util.Optional;

import com.lazaro.inventory.inventory.Inventory;
import com.lazaro.inventory.inventory.InventoryDTO;

public class InventoryConverter {

  public static Inventory convert(InventoryDTO inventoryDTO) {
    Inventory inventory = new Inventory();

    inventory.setQuantity(inventoryDTO.quantity());
    inventory.setLocationId(inventoryDTO.locationId());
    inventory.setProductId(inventoryDTO.productId());

    return inventory;
  }
  
  public static InventoryDTO convert(Inventory inventory) {
    InventoryDTO inventoryDTO = new InventoryDTO(
      inventory.getProductId(),
      inventory.getLocationId(),
      inventory.getQuantity()
    );

    return inventoryDTO;
  }

  public static InventoryDTO convert(Optional<Inventory> byId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'convert'");
  }

}
