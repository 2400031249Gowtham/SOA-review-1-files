package com.klu.tracking_service.service;

import com.klu.tracking_service.entity.TrackingEvent;
import com.klu.tracking_service.repository.TrackingEventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrackingService {

    private final TrackingEventRepository repository;

    public TrackingService(TrackingEventRepository repository) {
        this.repository = repository;
    }

    public TrackingEvent createEvent(TrackingEvent event) {
        return repository.save(event);
    }

    public List<TrackingEvent> getTrackingHistory(String trackingNumber) {
        return repository.findByTrackingNumberOrderByEventTimeDesc(
                trackingNumber
        );
    }

    public TrackingEvent getLatestTracking(String trackingNumber) {
        return repository.findFirstByTrackingNumberOrderByEventTimeDesc(
                trackingNumber
        );
    }
}
