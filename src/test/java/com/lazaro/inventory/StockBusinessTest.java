package com.lazaro.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lazaro.inventory.business.StockBusiness;
import com.lazaro.inventory.infra.exception.BusinessException;
import com.lazaro.inventory.inventory.Inventory;
import com.lazaro.inventory.inventory.InventoryRepository;
import com.lazaro.inventory.moviment.MovimentType;
import com.lazaro.inventory.moviment.StockMoviment;
import com.lazaro.inventory.moviment.StockMovimentDTO;
import com.lazaro.inventory.moviment.StockMovimentRepository;

@ExtendWith(MockitoExtension.class)
class StockBusinessTest {

    @Mock
    private StockMovimentRepository stockMovimentRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    private StockBusiness stockBusiness;

    private final UUID productId = UUID.randomUUID();
    private final UUID locationId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        stockBusiness = new StockBusiness(stockMovimentRepository, inventoryRepository);
    }

    @Test
    @DisplayName("Deve registrar entrada com sucesso e atualizar saldos prévio e novo")
    void shouldRegisterEntrySuccessfully() {
        Inventory existingInventory = new Inventory();
        existingInventory.setId(1L);
        existingInventory.setProductId(productId);
        existingInventory.setLocationId(locationId);
        existingInventory.setQuantity(50);
        existingInventory.setReservedQuantity(10);

        when(inventoryRepository.findByProductIdAndLocationId(productId, locationId))
                .thenReturn(Optional.of(existingInventory));
        when(inventoryRepository.save(any(Inventory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(stockMovimentRepository.save(any(StockMoviment.class)))
                .thenAnswer(invocation -> {
                    StockMoviment m = invocation.getArgument(0);
                    m.setIdMoviment(100L);
                    return m;
                });

        StockMovimentDTO dto = new StockMovimentDTO(
                null, productId, locationId, MovimentType.ENTRY, 30, "Recebimento de lote"
        );

        StockMovimentDTO result = stockBusiness.register(dto);

        assertNotNull(result);
        assertEquals(100L, result.idMoviment());
        assertEquals(50, result.previousQuantity());
        assertEquals(80, result.newQuantity());
        assertEquals(80, existingInventory.getQuantity());

        ArgumentCaptor<StockMoviment> captor = ArgumentCaptor.forClass(StockMoviment.class);
        verify(stockMovimentRepository).save(captor.capture());
        assertEquals(50, captor.getValue().getPreviousQuantity());
        assertEquals(80, captor.getValue().getNewQuantity());
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando saldo disponível for insuficiente para saída")
    void shouldThrowExceptionWhenInsufficientAvailableStockForExit() {
        Inventory existingInventory = new Inventory();
        existingInventory.setId(1L);
        existingInventory.setProductId(productId);
        existingInventory.setLocationId(locationId);
        existingInventory.setQuantity(50);
        existingInventory.setReservedQuantity(40); // Disponível = 50 - 40 = 10

        when(inventoryRepository.findByProductIdAndLocationId(productId, locationId))
                .thenReturn(Optional.of(existingInventory));

        StockMovimentDTO dto = new StockMovimentDTO(
                null, productId, locationId, MovimentType.EXIT, 15, "Venda checkout"
        );

        BusinessException ex = assertThrows(BusinessException.class, () -> stockBusiness.register(dto));
        assertNotNull(ex.getMessage());
    }

    @Test
    @DisplayName("Deve registrar ajuste de inventário com novo saldo físico")
    void shouldRegisterAdjustmentSuccessfully() {
        Inventory existingInventory = new Inventory();
        existingInventory.setId(1L);
        existingInventory.setProductId(productId);
        existingInventory.setLocationId(locationId);
        existingInventory.setQuantity(50);
        existingInventory.setReservedQuantity(0);

        when(inventoryRepository.findByProductIdAndLocationId(productId, locationId))
                .thenReturn(Optional.of(existingInventory));
        when(inventoryRepository.save(any(Inventory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(stockMovimentRepository.save(any(StockMoviment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        StockMovimentDTO dto = new StockMovimentDTO(
                null, productId, locationId, MovimentType.ADJUSTMENT, 42, "Balanço trimestral"
        );

        StockMovimentDTO result = stockBusiness.register(dto);

        assertEquals(50, result.previousQuantity());
        assertEquals(42, result.newQuantity());
        assertEquals(42, existingInventory.getQuantity());
    }

    @Test
    @DisplayName("Deve registrar transferência do pulmão para o principal com sucesso")
    void shouldRegisterTransferSuccessfully() {
        UUID destinationLocationId = UUID.randomUUID();

        Inventory sourceInventory = new Inventory();
        sourceInventory.setId(1L);
        sourceInventory.setProductId(productId);
        sourceInventory.setLocationId(locationId);
        sourceInventory.setQuantity(100);
        sourceInventory.setReservedQuantity(10); // Disponível = 90

        Inventory destinationInventory = new Inventory();
        destinationInventory.setId(2L);
        destinationInventory.setProductId(productId);
        destinationInventory.setLocationId(destinationLocationId);
        destinationInventory.setQuantity(5);
        destinationInventory.setReservedQuantity(0);

        when(inventoryRepository.findByProductIdAndLocationId(productId, locationId))
                .thenReturn(Optional.of(sourceInventory));
        when(inventoryRepository.findByProductIdAndLocationId(productId, destinationLocationId))
                .thenReturn(Optional.of(destinationInventory));
        when(inventoryRepository.save(any(Inventory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(stockMovimentRepository.save(any(StockMoviment.class)))
                .thenAnswer(invocation -> {
                    StockMoviment m = invocation.getArgument(0);
                    m.setIdMoviment(200L);
                    return m;
                });

        StockMovimentDTO dto = new StockMovimentDTO(
                null, productId, locationId, destinationLocationId, MovimentType.TRANSFER, 30, "Reabastecimento picking"
        );

        StockMovimentDTO result = stockBusiness.register(dto);

        assertNotNull(result);
        assertEquals(200L, result.idMoviment());
        assertEquals(destinationLocationId, result.destinationLocationId());
        assertEquals(100, result.previousQuantity());
        assertEquals(70, result.newQuantity());
        assertEquals(70, sourceInventory.getQuantity());
        assertEquals(35, destinationInventory.getQuantity());

        ArgumentCaptor<StockMoviment> captor = ArgumentCaptor.forClass(StockMoviment.class);
        verify(stockMovimentRepository).save(captor.capture());
        assertEquals(destinationLocationId, captor.getValue().getDestinationLocationId());
        assertEquals(100, captor.getValue().getPreviousQuantity());
        assertEquals(70, captor.getValue().getNewQuantity());
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando destinationLocationId for nulo em TRANSFER")
    void shouldThrowExceptionWhenDestinationLocationIsNullOnTransfer() {
        Inventory sourceInventory = new Inventory();
        sourceInventory.setProductId(productId);
        sourceInventory.setLocationId(locationId);
        sourceInventory.setQuantity(100);
        sourceInventory.setReservedQuantity(0);

        when(inventoryRepository.findByProductIdAndLocationId(productId, locationId))
                .thenReturn(Optional.of(sourceInventory));

        StockMovimentDTO dto = new StockMovimentDTO(
                null, productId, locationId, null, MovimentType.TRANSFER, 10, "Transferência sem destino"
        );

        BusinessException ex = assertThrows(BusinessException.class, () -> stockBusiness.register(dto));
        assertEquals("Localização de destino é obrigatória para movimentações do tipo TRANSFER.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando origem e destino forem iguais em TRANSFER")
    void shouldThrowExceptionWhenOriginAndDestinationAreSameOnTransfer() {
        Inventory sourceInventory = new Inventory();
        sourceInventory.setProductId(productId);
        sourceInventory.setLocationId(locationId);
        sourceInventory.setQuantity(100);
        sourceInventory.setReservedQuantity(0);

        when(inventoryRepository.findByProductIdAndLocationId(productId, locationId))
                .thenReturn(Optional.of(sourceInventory));

        StockMovimentDTO dto = new StockMovimentDTO(
                null, productId, locationId, locationId, MovimentType.TRANSFER, 10, "Origem igual destino"
        );

        BusinessException ex = assertThrows(BusinessException.class, () -> stockBusiness.register(dto));
        assertEquals("A localização de destino não pode ser igual à localização de origem.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando saldo disponível no pulmão for insuficiente")
    void shouldThrowExceptionWhenInsufficientStockInOriginOnTransfer() {
        Inventory sourceInventory = new Inventory();
        sourceInventory.setProductId(productId);
        sourceInventory.setLocationId(locationId);
        sourceInventory.setQuantity(50);
        sourceInventory.setReservedQuantity(40); // Disponível = 10

        when(inventoryRepository.findByProductIdAndLocationId(productId, locationId))
                .thenReturn(Optional.of(sourceInventory));

        UUID destinationLocationId = UUID.randomUUID();
        StockMovimentDTO dto = new StockMovimentDTO(
                null, productId, locationId, destinationLocationId, MovimentType.TRANSFER, 15, "Transferência excedente"
        );

        BusinessException ex = assertThrows(BusinessException.class, () -> stockBusiness.register(dto));
        assertNotNull(ex.getMessage());
    }
}
