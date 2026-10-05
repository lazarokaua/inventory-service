package com.lazaro.inventory.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.lazaro.inventory.business.StockBusiness;
import com.lazaro.inventory.moviment.StockMoviment;
import com.lazaro.inventory.moviment.StockMovimentDTO;
import com.lazaro.inventory.moviment.StockMovimentRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import jakarta.validation.Valid;

@RestController 
@RequestMapping("/moviment")
public class StockMovimentController {

    private final StockBusiness stockBusiness;
    private final StockMovimentRepository stockMovimentRepository;

    public StockMovimentController(StockBusiness stockBusiness, StockMovimentRepository stockMovimentRepository) {
        this.stockBusiness = stockBusiness;
        this.stockMovimentRepository = stockMovimentRepository;
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


    @GetMapping
    public ResponseEntity<Page<StockMoviment>> getAllMoviments(@PageableDefault(size = 10, sort = "date") Pageable pageable) {
        Page<StockMoviment> page = stockMovimentRepository.findAll(pageable);

        return ResponseEntity.ok(page);
    }

}
