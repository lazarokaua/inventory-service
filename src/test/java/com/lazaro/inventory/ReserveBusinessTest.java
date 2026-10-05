package com.lazaro.inventory;

import com.lazaro.inventory.business.ReserveBusiness;
import com.lazaro.inventory.infra.exception.BusinessException;
import com.lazaro.inventory.inventory.Inventory;
import com.lazaro.inventory.inventory.InventoryRepository;
import com.lazaro.inventory.reserve.Reserve;
import com.lazaro.inventory.reserve.ReserveDTO;
import com.lazaro.inventory.reserve.ReserveRepository;
import com.lazaro.inventory.reserve.ReserveResponseDTO;
import com.lazaro.inventory.reserve.ReserveStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReserveBusinessTest {

    @Mock
    private ReserveRepository reserveRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    private ReserveBusiness reserveBusiness;

    private final UUID productId = UUID.randomUUID();
    private final UUID locationId = UUID.randomUUID();
    private final UUID orderId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        reserveBusiness = new ReserveBusiness(reserveRepository, inventoryRepository);
    }

    @Test
    @DisplayName("Deve criar reserva com sucesso quando houver saldo disponível suficiente")
    void shouldCreateReserveSuccessfully() {
        Inventory inventory = new Inventory();
        inventory.setId(1L);
        inventory.setProductId(productId);
        inventory.setLocationId(locationId);
        inventory.setQuantity(100);
        inventory.setReservedQuantity(20);

        when(reserveRepository.findByOrderId(orderId)).thenReturn(Optional.empty());
        when(inventoryRepository.findByProductIdAndLocationId(productId, locationId))
                .thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(reserveRepository.save(any(Reserve.class)))
                .thenAnswer(invocation -> {
                    Reserve r = invocation.getArgument(0);
                    r.setIdReserva(10L);
                    return r;
                });

        ReserveDTO dto = new ReserveDTO(productId, locationId, 30, orderId);
        ReserveResponseDTO result = reserveBusiness.createReserve(dto);

        assertNotNull(result);
        assertEquals(10L, result.idReserva());
        assertEquals(productId, result.productId());
        assertEquals(locationId, result.locationId());
        assertEquals(30, result.quantity());
        assertEquals(ReserveStatus.PENDING, result.status());
        assertEquals(orderId, result.orderId());
        assertEquals(50, inventory.getReservedQuantity());

        ArgumentCaptor<Reserve> captor = ArgumentCaptor.forClass(Reserve.class);
        verify(reserveRepository).save(captor.capture());
        assertEquals(ReserveStatus.PENDING, captor.getValue().getStatus());
        assertEquals(30, captor.getValue().getQuantity());
        assertNotNull(captor.getValue().getExpiresAt());
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando já existir reserva pendente para o pedido")
    void shouldThrowExceptionWhenPendingReserveAlreadyExistsForOrder() {
        Reserve existing = Reserve.builder()
                .idReserva(1L)
                .orderId(orderId)
                .status(ReserveStatus.PENDING)
                .build();

        when(reserveRepository.findByOrderId(orderId)).thenReturn(Optional.of(existing));

        ReserveDTO dto = new ReserveDTO(productId, locationId, 10, orderId);

        BusinessException ex = assertThrows(BusinessException.class, () -> reserveBusiness.createReserve(dto));
        assertTrue(ex.getMessage().contains("Já existe uma reserva pendente"));
        verify(inventoryRepository, never()).findByProductIdAndLocationId(any(), any());
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando inventário não for encontrado")
    void shouldThrowExceptionWhenInventoryNotFoundOnCreate() {
        when(reserveRepository.findByOrderId(orderId)).thenReturn(Optional.empty());
        when(inventoryRepository.findByProductIdAndLocationId(productId, locationId))
                .thenReturn(Optional.empty());

        ReserveDTO dto = new ReserveDTO(productId, locationId, 10, orderId);

        BusinessException ex = assertThrows(BusinessException.class, () -> reserveBusiness.createReserve(dto));
        assertTrue(ex.getMessage().contains("Inventário não encontrado"));
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando saldo disponível for insuficiente para reserva")
    void shouldThrowExceptionWhenInsufficientAvailableStockOnCreate() {
        Inventory inventory = new Inventory();
        inventory.setProductId(productId);
        inventory.setLocationId(locationId);
        inventory.setQuantity(50);
        inventory.setReservedQuantity(40); // Disponível = 10

        when(reserveRepository.findByOrderId(orderId)).thenReturn(Optional.empty());
        when(inventoryRepository.findByProductIdAndLocationId(productId, locationId))
                .thenReturn(Optional.of(inventory));

        ReserveDTO dto = new ReserveDTO(productId, locationId, 15, orderId);

        BusinessException ex = assertThrows(BusinessException.class, () -> reserveBusiness.createReserve(dto));
        assertTrue(ex.getMessage().contains("Quantidade disponível insuficiente"));
        verify(inventoryRepository, never()).save(any());
        verify(reserveRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve confirmar reserva com sucesso debitando saldo físico e saldo reservado")
    void shouldConfirmReserveSuccessfully() {
        Reserve pendingReserve = Reserve.builder()
                .idReserva(5L)
                .productId(productId)
                .locationId(locationId)
                .quantity(20)
                .status(ReserveStatus.PENDING)
                .orderId(orderId)
                .expiresAt(LocalDateTime.now().plusHours(12))
                .build();

        Inventory inventory = new Inventory();
        inventory.setProductId(productId);
        inventory.setLocationId(locationId);
        inventory.setQuantity(100);
        inventory.setReservedQuantity(20);

        when(reserveRepository.findByOrderId(orderId)).thenReturn(Optional.of(pendingReserve));
        when(inventoryRepository.findByProductIdAndLocationId(productId, locationId))
                .thenReturn(Optional.of(inventory));
        when(reserveRepository.save(any(Reserve.class))).thenAnswer(i -> i.getArgument(0));

        ReserveResponseDTO result = reserveBusiness.confirmReserve(orderId);

        assertNotNull(result);
        assertEquals(ReserveStatus.CONFIRMED, result.status());
        assertEquals(80, inventory.getQuantity());
        assertEquals(0, inventory.getReservedQuantity());

        verify(inventoryRepository).save(inventory);
        verify(reserveRepository).save(pendingReserve);
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao tentar confirmar reserva que não está PENDING")
    void shouldThrowExceptionWhenConfirmingNonPendingReserve() {
        Reserve confirmedReserve = Reserve.builder()
                .idReserva(5L)
                .status(ReserveStatus.CONFIRMED)
                .orderId(orderId)
                .build();

        when(reserveRepository.findByOrderId(orderId)).thenReturn(Optional.of(confirmedReserve));

        BusinessException ex = assertThrows(BusinessException.class, () -> reserveBusiness.confirmReserve(orderId));
        assertTrue(ex.getMessage().contains("Apenas reservas com status PENDING podem ser confirmadas"));
    }

    @Test
    @DisplayName("Deve expirar reserva e liberar saldo reservado ao tentar confirmar reserva expirada")
    void shouldExpireReserveAndReleaseStockWhenExpired() {
        Reserve expiredReserve = Reserve.builder()
                .idReserva(5L)
                .productId(productId)
                .locationId(locationId)
                .quantity(20)
                .status(ReserveStatus.PENDING)
                .orderId(orderId)
                .expiresAt(LocalDateTime.now().minusMinutes(5))
                .build();

        Inventory inventory = new Inventory();
        inventory.setProductId(productId);
        inventory.setLocationId(locationId);
        inventory.setQuantity(100);
        inventory.setReservedQuantity(20);

        when(reserveRepository.findByOrderId(orderId)).thenReturn(Optional.of(expiredReserve));
        when(inventoryRepository.findByProductIdAndLocationId(productId, locationId))
                .thenReturn(Optional.of(inventory));

        BusinessException ex = assertThrows(BusinessException.class, () -> reserveBusiness.confirmReserve(orderId));
        assertTrue(ex.getMessage().contains("expirou e não pode ser confirmada"));
        assertEquals(ReserveStatus.EXPIRED, expiredReserve.getStatus());
        assertEquals(0, inventory.getReservedQuantity());
        assertEquals(100, inventory.getQuantity()); // saldo físico permanece intacto
    }

    @Test
    @DisplayName("Deve cancelar reserva com sucesso liberando o saldo reservado")
    void shouldCancelReserveSuccessfully() {
        Reserve pendingReserve = Reserve.builder()
                .idReserva(7L)
                .productId(productId)
                .locationId(locationId)
                .quantity(25)
                .status(ReserveStatus.PENDING)
                .orderId(orderId)
                .build();

        Inventory inventory = new Inventory();
        inventory.setProductId(productId);
        inventory.setLocationId(locationId);
        inventory.setQuantity(100);
        inventory.setReservedQuantity(35);

        when(reserveRepository.findByOrderId(orderId)).thenReturn(Optional.of(pendingReserve));
        when(inventoryRepository.findByProductIdAndLocationId(productId, locationId))
                .thenReturn(Optional.of(inventory));
        when(reserveRepository.save(any(Reserve.class))).thenAnswer(i -> i.getArgument(0));

        ReserveResponseDTO result = reserveBusiness.cancelReserve(orderId);

        assertNotNull(result);
        assertEquals(ReserveStatus.CANCELED, result.status());
        assertEquals(10, inventory.getReservedQuantity()); // 35 - 25 = 10
        assertEquals(100, inventory.getQuantity()); // Saldo físico não sofre alteração

        verify(inventoryRepository).save(inventory);
        verify(reserveRepository).save(pendingReserve);
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao tentar cancelar reserva com status diferente de PENDING")
    void shouldThrowExceptionWhenCancelingNonPendingReserve() {
        Reserve canceledReserve = Reserve.builder()
                .idReserva(7L)
                .status(ReserveStatus.CANCELED)
                .orderId(orderId)
                .build();

        when(reserveRepository.findByOrderId(orderId)).thenReturn(Optional.of(canceledReserve));

        BusinessException ex = assertThrows(BusinessException.class, () -> reserveBusiness.cancelReserve(orderId));
        assertTrue(ex.getMessage().contains("Apenas reservas com status PENDING podem ser canceladas"));
    }

    @Test
    @DisplayName("Deve buscar reserva por orderId com sucesso")
    void shouldFindReserveByOrderIdSuccessfully() {
        Reserve reserve = Reserve.builder()
                .idReserva(1L)
                .productId(productId)
                .locationId(locationId)
                .quantity(10)
                .status(ReserveStatus.PENDING)
                .orderId(orderId)
                .build();

        when(reserveRepository.findByOrderId(orderId)).thenReturn(Optional.of(reserve));

        ReserveResponseDTO result = reserveBusiness.findByOrderId(orderId);

        assertNotNull(result);
        assertEquals(orderId, result.orderId());
        assertEquals(10, result.quantity());
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao buscar reserva inexistente por orderId")
    void shouldThrowExceptionWhenReserveNotFoundByOrderId() {
        when(reserveRepository.findByOrderId(orderId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> reserveBusiness.findByOrderId(orderId));
        assertTrue(ex.getMessage().contains("Reserva não encontrada"));
    }
}
