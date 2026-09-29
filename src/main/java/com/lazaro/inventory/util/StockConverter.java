package com.lazaro.inventory.util;

import com.lazaro.inventory.moviment.StockMoviment;
import com.lazaro.inventory.moviment.StockMovimentDTO;

public class StockConverter {

  public static StockMoviment convert(StockMovimentDTO dto) {
    StockMoviment moviment = new StockMoviment();
    
    moviment.setIdMoviment(dto.idMoviment());
    moviment.setProductId(dto.productId());
    moviment.setLocationId(dto.locationId());
    moviment.setDestinationLocationId(dto.destinationLocationId());
    moviment.setMovimentType(dto.movimentType());
    moviment.setQuantity(dto.quantity());
    moviment.setReason(dto.reason());
    moviment.setPreviousQuantity(dto.previousQuantity());
    moviment.setNewQuantity(dto.newQuantity());
    moviment.setCreatedAt(dto.createdAt());

    return moviment;
  }
  
  public static StockMovimentDTO convert(StockMoviment stockMoviment) {
    return new StockMovimentDTO(
      stockMoviment.getIdMoviment(),
      stockMoviment.getProductId(),
      stockMoviment.getLocationId(),
      stockMoviment.getDestinationLocationId(),
      stockMoviment.getMovimentType(),
      stockMoviment.getQuantity(),
      stockMoviment.getReason(),
      stockMoviment.getPreviousQuantity(),
      stockMoviment.getNewQuantity(),
      stockMoviment.getCreatedAt()
    );
  }

}
