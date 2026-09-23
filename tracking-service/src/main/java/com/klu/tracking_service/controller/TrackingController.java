package com.klu.tracking_service.controller;

import com.klu.tracking_service.entity.TrackingEvent;
import com.klu.tracking_service.service.TrackingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tracking")
public class TrackingController {

    private final TrackingService trackingService;

    public TrackingController(TrackingService trackingService) {
        this.trackingService = trackingService;
    }

    @PostMapping
    public ResponseEntity<TrackingEvent> createTrackingEvent(
            @RequestBody TrackingEvent event) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(trackingService.createEvent(event));
    }

    @GetMapping("/{trackingNumber}")
    public ResponseEntity<List<TrackingEvent>> getTrackingHistory(
            @PathVariable String trackingNumber) {

        return ResponseEntity.ok(
                trackingService.getTrackingHistory(trackingNumber)
        );
    }

    @GetMapping("/{trackingNumber}/latest")
    public ResponseEntity<?> getLatestTracking(
            @PathVariable String trackingNumber) {

        TrackingEvent event =
                trackingService.getLatestTracking(trackingNumber);

        if (event == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error",
                            "Tracking information not found"
                    ));
        }

        return ResponseEntity.ok(event);
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {

        return ResponseEntity.ok(
                Map.of(
                        "status", "UP",
                        "service", "tracking-service"
                )
        );
    }
}
