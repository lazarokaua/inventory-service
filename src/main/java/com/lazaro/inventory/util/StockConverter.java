package com.lazaro.inventory.util;

import com.lazaro.inventory.moviment.StockMoviment;
import com.lazaro.inventory.moviment.StockMovimentDTO;

public class StockConverter {

  public static StockMoviment convert(StockMovimentDTO stockMovimentDTO) {
    StockMoviment stockMoviment = new StockMoviment();

    stockMoviment.setQuantity(stockMovimentDTO.quantity());
    stockMoviment.setLocationId(stockMovimentDTO.locationId());
    stockMoviment.setProductId(stockMovimentDTO.productId());

    return stockMoviment;
  }
  

 public static StockMovimentDTO convert(StockMoviment stockMoviment) {
    StockMovimentDTO stockMovimentDTO = new StockMovimentDTO(
      stockMoviment.getIdMoviment(),
      stockMoviment.getProductId(),
      stockMoviment.getLocationId(),
      stockMoviment.getMovimentType(),
      stockMoviment.getQuantity(),
      stockMoviment.getReason()
    );

    return stockMovimentDTO;
  }

}
