package com.lazaro.inventory.controller;

import com.lazaro.inventory.business.InventoryBusiness;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public InventoryDTO createInventory(@RequestBody @Valid InventoryDTO inventoryDTO) {
        return inventoryBusiness.create(inventoryDTO);
    }
}
