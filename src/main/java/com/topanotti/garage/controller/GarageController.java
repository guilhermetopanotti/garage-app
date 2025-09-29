package com.topanotti.garage.controller;

import com.topanotti.garage.dto.GarageEventDTO;
import com.topanotti.garage.dto.VehichleDTO;
import com.topanotti.garage.model.GarageEvent;
import com.topanotti.garage.model.Spot;
import com.topanotti.garage.model.enums.GarageEventType;
import com.topanotti.garage.service.GarageService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/garage")
public class GarageController {

    private final GarageService garageService;

    public GarageController(GarageService garageService) {
        this.garageService = garageService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<GarageEvent> handleWebhook(@RequestBody GarageEventDTO dto) {
        return ResponseEntity.ok(garageService.handleEvent(dto));
    }

    @GetMapping("/plate-status")
    public ResponseEntity<?> getPlateStatus(@RequestParam String plate) {
        Optional<GarageEvent> status = garageService.getPlateStatus(plate);
        if (status.isPresent()) {
            VehichleDTO vehichleDTO = VehichleDTO.builder()
                    .vehiclePlate(status.get().getVehiclePlate())
                    .eventType(GarageEventType.PARKED)
                    .build();
            return ResponseEntity.ok(vehichleDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/spot-status")
    public ResponseEntity<List<Spot>> getSpotStatus(@RequestParam(required = false) String sector) {
        if (sector != null) {
            return ResponseEntity.ok(garageService.getSpots(sector));
        } else {
            return ResponseEntity.ok(garageService.getAllSpots());
        }
    }

    @GetMapping("/revenue")
    public ResponseEntity<Double> getRevenue(
            @RequestParam String sector,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        double revenue = garageService.getRevenue(sector, date);
        return ResponseEntity.ok(revenue);
    }
}
