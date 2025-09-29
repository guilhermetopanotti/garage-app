package com.topanotti.garage.repository;

import com.topanotti.garage.model.Spot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SpotRepository extends JpaRepository<Spot, Long> {

    Optional<Spot> findFirstByOccupiedFalse();

    Optional<Spot> findFirstBySectorAndOccupiedFalse(String sector);

    Collection<Spot> findBySectorAndOccupiedTrue(String sector);

    List<Spot> findBySector(String sector);

    Optional<Spot> findFirstByVehiclePlate(String vehiclePlate);
}