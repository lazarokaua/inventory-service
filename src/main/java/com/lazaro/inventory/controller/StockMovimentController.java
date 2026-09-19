package com.lazaro.inventory.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.lazaro.inventory.business.StockBusiness;
import com.lazaro.inventory.moviment.StockMovimentDTO;

import jakarta.validation.Valid;

@RestController 
@RequestMapping("/moviment")
public class StockMovimentController {

    private final StockBusiness stockBusiness;

    public StockMovimentController(StockBusiness stockBusiness) {
        this.stockBusiness = stockBusiness;
    }
    

    @PostMapping
    public ResponseEntity<StockMovimentDTO> createStockMoviment(@RequestBody @Valid StockMovimentDTO stockMovimentDTO) {

        StockMovimentDTO sMovimentDTO = stockBusiness.register(stockMovimentDTO);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{idMoviment}")
        .buildAndExpand(sMovimentDTO.idMoviment())
        .toUri();

        return ResponseEntity.created(location).body(sMovimentDTO);



    }

}
