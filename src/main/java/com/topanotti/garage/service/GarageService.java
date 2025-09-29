package com.topanotti.garage.service;

import com.topanotti.garage.dto.GarageEventDTO;
import com.topanotti.garage.exception.ParkingFullException;
import com.topanotti.garage.exception.SectorNotFoundException;
import com.topanotti.garage.exception.VehicleAlreadyParkedException;
import com.topanotti.garage.exception.VehicleNotFoundException;
import com.topanotti.garage.model.GarageEvent;
import com.topanotti.garage.model.GarageSector;
import com.topanotti.garage.model.Spot;
import com.topanotti.garage.model.enums.GarageEventType;
import com.topanotti.garage.repository.GarageEventRepository;
import com.topanotti.garage.repository.GarageSectorRepository;
import com.topanotti.garage.repository.SpotRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GarageService {

    private final SpotRepository spotRepository;
    private final GarageEventRepository garageEventRepository;
    private final GarageSectorRepository garageSectorRepository;

    private final Map<String, GarageSector> sectorCache = new HashMap<>();

    public GarageService(SpotRepository spotRepository,
                         GarageEventRepository garageEventRepository,
                         GarageSectorRepository garageSectorRepository) {
        this.spotRepository = spotRepository;
        this.garageEventRepository = garageEventRepository;
        this.garageSectorRepository = garageSectorRepository;

        garageSectorRepository.findAll()
                .forEach(s -> sectorCache.put(s.getSector(), s));
    }

    public GarageEvent handleEvent(GarageEventDTO dto) {
        if (dto.getEventType() == GarageEventType.ENTRY) {
            return handleEntry(dto);
        } else if (dto.getEventType() == GarageEventType.EXIT) {
            return handleExit(dto);
        } else {
            throw new IllegalArgumentException("Unsupported event type: " + dto.getEventType());
        }
    }

    private GarageEvent handleEntry(GarageEventDTO dto) {
        spotRepository.findFirstByVehiclePlate(dto.getVehiclePlate())
                .ifPresent(s -> {
                    throw new VehicleAlreadyParkedException("Vehicle already parked: " + dto.getVehiclePlate());
                });

        Spot occupiedSpot = occupySpot(dto.getVehiclePlate());

        GarageEvent event = new GarageEvent();
        event.setVehiclePlate(dto.getVehiclePlate());
        event.setEventType(GarageEventType.ENTRY);
        event.setEntryTime(dto.getEntryTime());
        event.setSector(occupiedSpot.getSector());

        return garageEventRepository.save(event);
    }


    private GarageEvent handleExit(GarageEventDTO dto) {
        GarageEvent lastEntry = garageEventRepository
                .findTopByVehiclePlateAndEventTypeOrderByEntryTimeDesc(dto.getVehiclePlate(), GarageEventType.ENTRY)
                .orElseThrow(() -> new VehicleNotFoundException("Entry event not found for vehicle: " + dto.getVehiclePlate()));

        releaseSpot(lastEntry);
        double price = calculate(lastEntry, Duration.between(lastEntry.getEntryTime(), dto.getExitTime()));

        lastEntry.setExitTime(dto.getExitTime());
        lastEntry.setEventType(GarageEventType.EXIT);
        lastEntry.setPrice(price);

        return garageEventRepository.save(lastEntry);
    }

    private Spot occupySpot(String plate) {
        List<GarageSector> sectors = garageSectorRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(GarageSector::getSector))
                .collect(Collectors.toList());

        for (GarageSector sector : sectors) {
            long occupied = spotRepository.findBySectorAndOccupiedTrue(sector.getSector()).size();
            if (occupied >= sector.getMaxCapacity()) continue;

            Optional<Spot> spotOpt = spotRepository.findFirstBySectorAndOccupiedFalse(sector.getSector());
            if (spotOpt.isPresent()) {
                Spot spot = spotOpt.get();
                spot.setOccupied(true);
                spot.setVehiclePlate(plate);
                spotRepository.save(spot);
                return spot;
            }
        }

        throw new ParkingFullException("No available spots in any sector");
    }

    private void releaseSpot(GarageEvent event){
        String plate = event.getVehiclePlate();
        String sector = event.getSector();

        Spot spot = spotRepository.findFirstByVehiclePlate(plate)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found: " + plate));

        if (!spot.getSector().equals(sector)) {
            throw new IllegalStateException("Spot sector does not match event sector");
        }
        spot.setOccupied(false);
        spot.setVehiclePlate(null);
        spotRepository.save(spot);
    }

    private Double calculate(GarageEvent event, Duration duration) {
        GarageSector garageSector = garageSectorRepository.findBySector(event.getSector())
                .orElseThrow(() -> new SectorNotFoundException("Sector not found: " + event.getSector()));

        long minutes = duration.toMinutes();

        double price;
        if (minutes <= 30) {
            price = 0;
        } else {
            double hours = Math.ceil((minutes - 30) / 60.0);
            price = garageSector.getBasePrice() * hours;

            long occupied = spotRepository.findBySectorAndOccupiedTrue(garageSector.getSector()).size();
            double occupancyRate = occupied / (double) garageSector.getMaxCapacity();

            if (occupancyRate < 0.25) price *= 0.9;
            else if (occupancyRate < 0.5) price *= 1.0;
            else if (occupancyRate < 0.75) price *= 1.1;
            else price *= 1.25;
        }

        return price;
    }

    public List<Spot> getSpots(String sector) {
        return spotRepository.findBySector(sector);
    }

    public List<Spot> getAllSpots() {
        return spotRepository.findAll();
    }

    public Optional<GarageEvent> getPlateStatus(String plate) {
        return garageEventRepository.findTopByVehiclePlateOrderByEntryTimeDesc(plate);
    }

    public double getRevenue(String sector, LocalDate date) {
        return garageEventRepository.findByEventTypeAndExitTimeBetween(
                        GarageEventType.EXIT,
                        date.atStartOfDay(),
                        date.plusDays(1).atStartOfDay())
                .stream()
                .filter(e -> sector == null || sector.equals(e.getSector()))
                .mapToDouble(GarageEvent::getPrice)
                .sum();
    }
}
