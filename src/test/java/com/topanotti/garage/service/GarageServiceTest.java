package com.topanotti.garage.service;

import com.topanotti.garage.dto.GarageEventDTO;
import com.topanotti.garage.exception.*;
import com.topanotti.garage.model.*;
import com.topanotti.garage.model.enums.GarageEventType;
import com.topanotti.garage.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GarageServiceTest {

    @Mock private SpotRepository spotRepository;
    @Mock private GarageEventRepository garageEventRepository;
    @Mock private GarageSectorRepository garageSectorRepository;

    @InjectMocks private GarageService garageService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        GarageSector sectorA = new GarageSector();
        sectorA.setSector("A");
        sectorA.setMaxCapacity(10);
        sectorA.setBasePrice(10.0);

        when(garageSectorRepository.findAll()).thenReturn(List.of(sectorA));
        when(garageSectorRepository.findBySector("A")).thenReturn(Optional.of(sectorA));
    }

    @Test
    void shouldThrowWhenVehicleAlreadyParked() {
        GarageEventDTO dto = GarageEventDTO.builder()
                .vehiclePlate("ABC-1234")
                .eventType(GarageEventType.ENTRY)
                .entryTime(LocalDateTime.now()).build();

        when(spotRepository.findFirstByVehiclePlate("ABC-1234"))
                .thenReturn(Optional.of(new Spot()));

        assertThrows(VehicleAlreadyParkedException.class,
                () -> garageService.handleEvent(dto));
    }

    @Test
    void shouldHandleEntrySuccessfully() {
        GarageEventDTO dto = GarageEventDTO.builder()
                .vehiclePlate("XYZ-9999")
                .eventType(GarageEventType.ENTRY)
                .entryTime(LocalDateTime.now()).build();

        Spot spot = new Spot();
        spot.setSector("A");
        spot.setOccupied(false);

        when(spotRepository.findFirstByVehiclePlate("XYZ-9999"))
                .thenReturn(Optional.empty());
        when(spotRepository.findFirstBySectorAndOccupiedFalse("A"))
                .thenReturn(Optional.of(spot));
        when(garageEventRepository.save(any(GarageEvent.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        GarageEvent event = garageService.handleEvent(dto);

        assertNotNull(event);
        assertEquals("XYZ-9999", event.getVehiclePlate());
        assertEquals(GarageEventType.ENTRY, event.getEventType());
        verify(spotRepository).save(any(Spot.class));
    }

    @Test
    void shouldThrowWhenNoSpotsAvailable() {
        GarageEventDTO dto = GarageEventDTO.builder()
                .vehiclePlate("ZZZ-0000")
                .eventType(GarageEventType.ENTRY)
                .entryTime(LocalDateTime.now()).build();

        when(spotRepository.findFirstBySectorAndOccupiedFalse("A"))
                .thenReturn(Optional.empty());

        assertThrows(ParkingFullException.class,
                () -> garageService.handleEvent(dto));
    }

    @Test
    void shouldHandleExitSuccessfully() {
        LocalDateTime entryTime = LocalDateTime.now().minusHours(2);
        LocalDateTime exitTime = LocalDateTime.now();

        GarageEventDTO dto = GarageEventDTO.builder()
                .vehiclePlate("CAR-1234")
                .eventType(GarageEventType.EXIT)
                .exitTime(exitTime).build();

        GarageEvent entryEvent = new GarageEvent();
        entryEvent.setVehiclePlate("CAR-1234");
        entryEvent.setSector("A");
        entryEvent.setEntryTime(entryTime);
        entryEvent.setEventType(GarageEventType.ENTRY);

        Spot spot = new Spot();
        spot.setSector("A");
        spot.setOccupied(true);
        spot.setVehiclePlate("CAR-1234");

        when(garageEventRepository.findTopByVehiclePlateAndEventTypeOrderByEntryTimeDesc("CAR-1234", GarageEventType.ENTRY))
                .thenReturn(Optional.of(entryEvent));
        when(spotRepository.findFirstByVehiclePlate("CAR-1234"))
                .thenReturn(Optional.of(spot));
        when(garageEventRepository.save(any(GarageEvent.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        GarageEvent exitEvent = garageService.handleEvent(dto);

        assertNotNull(exitEvent.getExitTime());
        assertEquals(GarageEventType.EXIT, exitEvent.getEventType());
        assertTrue(exitEvent.getPrice() > 0);
        verify(spotRepository).save(any(Spot.class));
    }
}
