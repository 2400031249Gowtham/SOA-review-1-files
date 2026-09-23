package com.klu.tracking_service.repository;

import com.klu.tracking_service.entity.TrackingEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrackingEventRepository extends JpaRepository<TrackingEvent, Long> {

    List<TrackingEvent> findByTrackingNumberOrderByEventTimeDesc(
            String trackingNumber
    );

    TrackingEvent findFirstByTrackingNumberOrderByEventTimeDesc(
            String trackingNumber
    );
}
