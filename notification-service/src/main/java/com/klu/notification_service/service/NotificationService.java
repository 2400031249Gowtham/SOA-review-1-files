package com.klu.notification_service.service;

import com.klu.notification_service.entity.Notification;
import com.klu.notification_service.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    public Notification createNotification(Notification notification) {
        return repository.save(notification);
    }

    public List<Notification> getNotifications(String trackingNumber) {
        return repository.findByTrackingNumberOrderByCreatedAtDesc(
                trackingNumber
        );
    }
}
