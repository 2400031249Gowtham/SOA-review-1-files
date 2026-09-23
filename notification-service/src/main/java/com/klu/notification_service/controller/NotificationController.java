package com.klu.notification_service.controller;

import com.klu.notification_service.entity.Notification;
import com.klu.notification_service.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService) {

        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<Notification> createNotification(
            @RequestBody Notification notification) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        notificationService.createNotification(
                                notification
                        )
                );
    }

    @GetMapping("/{trackingNumber}")
    public ResponseEntity<List<Notification>> getNotifications(
            @PathVariable String trackingNumber) {

        return ResponseEntity.ok(
                notificationService.getNotifications(trackingNumber)
        );
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {

        return ResponseEntity.ok(
                Map.of(
                        "status", "UP",
                        "service", "notification-service"
                )
        );
    }
}
