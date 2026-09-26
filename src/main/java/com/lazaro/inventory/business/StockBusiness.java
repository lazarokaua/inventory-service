package com.lazaro.inventory.business;

import org.springframework.stereotype.Service;

import com.lazaro.inventory.infra.exception.BusinessException;
import com.lazaro.inventory.inventory.Inventory;
import com.lazaro.inventory.inventory.InventoryRepository;
import com.lazaro.inventory.moviment.StockMoviment;
import com.lazaro.inventory.moviment.StockMovimentDTO;
import com.lazaro.inventory.moviment.StockMovimentRepository;
import com.lazaro.inventory.util.StockConverter;

import jakarta.transaction.Transactional;


@Service
public class StockBusiness {

    private final StockMovimentRepository stockMovimentRepository;
    private final InventoryRepository inventoryRepository;

    public StockBusiness(StockMovimentRepository stockMovimentRepository, InventoryRepository inventoryRepository) {
        this.stockMovimentRepository = stockMovimentRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public StockMovimentDTO register(StockMovimentDTO stockMovimentDTO) {

        Inventory inventory = inventoryRepository
                .findByProductIdAndLocationId(stockMovimentDTO.productId(), stockMovimentDTO.locationId())
                .orElseGet(() -> {
                    var newInventory = new Inventory();
                    newInventory.setProductId(stockMovimentDTO.productId());
                    newInventory.setLocationId(stockMovimentDTO.locationId());
                    newInventory.setQuantity(0);
                    newInventory.setReservedQuantity(0);
                    return newInventory;
                });

        int previousQuantity = inventory.getQuantity() != null ? inventory.getQuantity() : 0;
        int reservedQuantity = inventory.getReservedQuantity() != null ? inventory.getReservedQuantity() : 0;
        int availableQuantity = previousQuantity - reservedQuantity;
        int newQuantity;

        switch (stockMovimentDTO.movimentType()) {
            case ENTRY -> newQuantity = previousQuantity + stockMovimentDTO.quantity();
            case EXIT -> {
                if (availableQuantity < stockMovimentDTO.quantity()) {
                    throw new BusinessException("Estoque disponível insuficiente para saída. Disponível: "
                            + availableQuantity + " (Físico: " + previousQuantity + ", Reservado: " + reservedQuantity
                            + "), Solicitado: " + stockMovimentDTO.quantity());
                }
                newQuantity = previousQuantity - stockMovimentDTO.quantity();
            }
            case ADJUSTMENT -> newQuantity = stockMovimentDTO.quantity();
            case TRANSFER -> throw new BusinessException("A movimentação TRANSFER exige fluxo de transferência entre localizações.");
            default -> throw new BusinessException("Tipo de movimentação não suportado: " + stockMovimentDTO.movimentType());
        }

        inventory.setQuantity(newQuantity);
        inventoryRepository.save(inventory);

        StockMoviment moviment = StockConverter.convert(stockMovimentDTO);
        moviment.setPreviousQuantity(previousQuantity);
        moviment.setNewQuantity(newQuantity);

        StockMoviment savedMoviment = stockMovimentRepository.save(moviment);
        return StockConverter.convert(savedMoviment);
    }



}
