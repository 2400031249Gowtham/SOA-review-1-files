package com.klu.notification_service.repository;

import com.klu.notification_service.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByTrackingNumberOrderByCreatedAtDesc(
            String trackingNumber
    );
}
