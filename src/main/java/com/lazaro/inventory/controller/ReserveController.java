package com.lazaro.inventory.controller;

import com.lazaro.inventory.business.ReserveBusiness;
import com.lazaro.inventory.reserve.ReserveDTO;
import com.lazaro.inventory.reserve.ReserveResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/reserves")
public class ReserveController {

  private final ReserveBusiness reserveBusiness;

  public ReserveController(ReserveBusiness reserveBusiness) {
    this.reserveBusiness = reserveBusiness;
  }

  @PostMapping
  public ResponseEntity<ReserveResponseDTO> createReserve(@RequestBody @Valid ReserveDTO reserveDTO) {
    ReserveResponseDTO response = reserveBusiness.createReserve(reserveDTO);
    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(response.idReserva())
            .toUri();
    return ResponseEntity.created(location).body(response);
  }

  @PostMapping("/order/{orderId}/confirm")
  public ResponseEntity<ReserveResponseDTO> confirmReserve(@PathVariable UUID orderId) {
    ReserveResponseDTO response = reserveBusiness.confirmReserve(orderId);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/order/{orderId}/cancel")
  public ResponseEntity<ReserveResponseDTO> cancelReserve(@PathVariable UUID orderId) {
    ReserveResponseDTO response = reserveBusiness.cancelReserve(orderId);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/order/{orderId}")
  public ResponseEntity<ReserveResponseDTO> getByOrderId(@PathVariable UUID orderId) {
    ReserveResponseDTO response = reserveBusiness.findByOrderId(orderId);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ReserveResponseDTO> getById(@PathVariable Long id) {
    ReserveResponseDTO response = reserveBusiness.findById(id);
    return ResponseEntity.ok(response);
  }
}
