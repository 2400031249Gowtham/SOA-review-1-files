package com.klu.shipment_service.service;

import com.klu.shipment_service.entity.Shipment;
import com.klu.shipment_service.repository.ShipmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;

    public ShipmentService(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    public Shipment createShipment(Shipment shipment) {

        if (shipment.getTrackingNumber() == null ||
                shipment.getTrackingNumber().isBlank()) {

            shipment.setTrackingNumber(generateTrackingNumber());
        }

        if (shipmentRepository.existsByTrackingNumber(shipment.getTrackingNumber())) {
            throw new IllegalArgumentException(
                    "Tracking number already exists"
            );
        }

        if (shipment.getStatus() == null ||
                shipment.getStatus().isBlank()) {

            shipment.setStatus("BOOKED");
        }

        return shipmentRepository.save(shipment);
    }

    public Shipment getShipmentById(Long id) {

        return shipmentRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Shipment not found with id: " + id
                        ));
    }

    public Shipment getShipmentByTrackingNumber(String trackingNumber) {

        return shipmentRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Shipment not found with tracking number: "
                                        + trackingNumber
                        ));
    }

    public List<Shipment> getAllShipments() {

        return shipmentRepository.findAll();
    }

    public Shipment updateShipmentStatus(Long id, String status) {

        Shipment shipment = getShipmentById(id);

        shipment.setStatus(status);

        return shipmentRepository.save(shipment);
    }

    public void deleteShipment(Long id) {

        if (!shipmentRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "Shipment not found with id: " + id
            );
        }

        shipmentRepository.deleteById(id);
    }

    private String generateTrackingNumber() {

        return "SWIFT-" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();
    }
}
