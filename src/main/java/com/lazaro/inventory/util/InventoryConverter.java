package com.lazaro.inventory.util;



import com.lazaro.inventory.inventory.Inventory;
import com.lazaro.inventory.inventory.InventoryDTO;

public class InventoryConverter {

  public static Inventory convert(InventoryDTO inventoryDTO) {
    Inventory inventory = new Inventory();

    inventory.setId(inventoryDTO.id());
    inventory.setProductId(inventoryDTO.productId());
    inventory.setLocationId(inventoryDTO.locationId());
    inventory.setQuantity(inventoryDTO.quantity());
    inventory.setReservedQuantity(inventoryDTO.reservedQuantity() != null ? inventoryDTO.reservedQuantity() : 0);

    return inventory;
  }
  
  public static InventoryDTO convert(Inventory inventory) {
    return new InventoryDTO(
      inventory.getId(),
      inventory.getProductId(),
      inventory.getLocationId(),
      inventory.getQuantity(),
      inventory.getReservedQuantity()
    );
  }

 

}
