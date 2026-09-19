package com.lazaro.inventory.business;

import org.springframework.stereotype.Service;

import com.lazaro.inventory.moviment.StockMovimentDTO;
import com.lazaro.inventory.moviment.StockMovimentRepository;
import com.lazaro.inventory.util.StockConverter;


@Service 
public class StockBusiness {


    private final StockMovimentRepository stockMovimentRepository;

    public StockBusiness(StockMovimentRepository stockMovimentRepository) {
        this.stockMovimentRepository = stockMovimentRepository;
    }

    public StockMovimentDTO register(StockMovimentDTO stockMovimentDTO) {
        return StockConverter.convert(
            stockMovimentRepository.save(StockConverter.convert(stockMovimentDTO))
        );

    }



}
