package com.klu.shipment_service.controller;

import com.klu.shipment_service.entity.Shipment;
import com.klu.shipment_service.service.ShipmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @PostMapping
    public ResponseEntity<?> createShipment(
            @Valid @RequestBody Shipment shipment) {

        try {
            Shipment createdShipment =
                    shipmentService.createShipment(shipment);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(createdShipment);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getShipmentById(
            @PathVariable Long id) {

        try {
            return ResponseEntity.ok(
                    shipmentService.getShipmentById(id)
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/tracking/{trackingNumber}")
    public ResponseEntity<?> getShipmentByTrackingNumber(
            @PathVariable String trackingNumber) {

        try {
            return ResponseEntity.ok(
                    shipmentService
                            .getShipmentByTrackingNumber(trackingNumber)
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Shipment>> getAllShipments() {

        return ResponseEntity.ok(
                shipmentService.getAllShipments()
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateShipmentStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        try {
            return ResponseEntity.ok(
                    shipmentService
                            .updateShipmentStatus(id, status)
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteShipment(
            @PathVariable Long id) {

        try {
            shipmentService.deleteShipment(id);

            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "Shipment deleted successfully"
                    )
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {

        return ResponseEntity.ok(
                Map.of(
                        "status", "UP",
                        "service", "shipment-service"
                )
        );
    }
}
