package com.lazaro.inventory.controller;

import com.lazaro.inventory.business.InventoryBusiness;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.lazaro.inventory.inventory.InventoryDTO;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryBusiness inventoryBusiness;

    public InventoryController(InventoryBusiness inventoryBusiness){
      this.inventoryBusiness = inventoryBusiness;
    }

    @PostMapping
    public ResponseEntity<InventoryDTO> createInventory(@RequestBody @Valid InventoryDTO inventoryDTO) {
        InventoryDTO createdDto = inventoryBusiness.create(inventoryDTO);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{productId}")
        .buildAndExpand(createdDto.productId())
        .toUri();

        return ResponseEntity.created(location).body(createdDto);

    }

    @GetMapping("/{id}")
    public  ResponseEntity<InventoryDTO> getById(@PathVariable Long id){
        InventoryDTO inventoryDTO = inventoryBusiness.findById(id);

        return ResponseEntity.ok(inventoryDTO);
        
    }

    @GetMapping
    public  ResponseEntity<List<InventoryDTO>> getAll(){
        List<InventoryDTO> inventoryDTO = inventoryBusiness.findAll();

        return ResponseEntity.ok(inventoryDTO);
        
    }
}
