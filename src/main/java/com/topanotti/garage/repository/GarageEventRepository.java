package com.topanotti.garage.repository;

import com.topanotti.garage.model.GarageEvent;
import com.topanotti.garage.model.enums.GarageEventType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface GarageEventRepository extends JpaRepository<GarageEvent, Long> {

    Optional<GarageEvent> findTopByVehiclePlateAndEventTypeOrderByEntryTimeDesc(String vehiclePlate, GarageEventType exit);

    List<GarageEvent> findByEventTypeAndExitTimeBetween(GarageEventType exit, LocalDateTime atStartOfDay, LocalDateTime atStartOfDay1);

    Optional<GarageEvent> findTopByVehiclePlateOrderByEntryTimeDesc(String plate);
}