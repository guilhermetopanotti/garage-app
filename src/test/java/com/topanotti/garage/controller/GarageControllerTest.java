package com.topanotti.garage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.topanotti.garage.dto.GarageEventDTO;
import com.topanotti.garage.model.GarageEvent;
import com.topanotti.garage.model.Spot;
import com.topanotti.garage.model.enums.GarageEventType;
import com.topanotti.garage.service.GarageService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GarageController.class)
class GarageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GarageService garageService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void handleWebhook_ShouldReturnGarageEvent() throws Exception {
        GarageEventDTO dto = GarageEventDTO.builder()
                .vehiclePlate("ABC1234")
                .eventType(GarageEventType.ENTRY)
                .entryTime(LocalDateTime.now()).build();

        GarageEvent event = new GarageEvent();
        event.setVehiclePlate("ABC1234");
        event.setEventType(GarageEventType.ENTRY);

        Mockito.when(garageService.handleEvent(Mockito.any(GarageEventDTO.class))).thenReturn(event);

        mockMvc.perform(post("/garage/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehiclePlate", is("ABC1234")))
                .andExpect(jsonPath("$.eventType", is("ENTRY")));
    }

    @Test
    void getPlateStatus_ShouldReturnParked() throws Exception {
        GarageEvent event = new GarageEvent();
        event.setVehiclePlate("XYZ9876");
        event.setEventType(GarageEventType.ENTRY);

        Mockito.when(garageService.getPlateStatus("XYZ9876")).thenReturn(Optional.of(event));

        mockMvc.perform(get("/garage/plate-status")
                        .param("plate", "XYZ9876"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehiclePlate", is("XYZ9876")))
                .andExpect(jsonPath("$.eventType", is("PARKED"))); // importante: controller força PARKED
    }

    @Test
    void getPlateStatus_ShouldReturnNotFound() throws Exception {
        Mockito.when(garageService.getPlateStatus("NOTFOUND")).thenReturn(Optional.empty());

        mockMvc.perform(get("/garage/plate-status")
                        .param("plate", "NOTFOUND"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getSpotStatus_ShouldReturnAllSpots() throws Exception {
        Spot spot = new Spot();
        spot.setId(1L);
        spot.setSector("A");
        spot.setOccupied(false);

        Mockito.when(garageService.getAllSpots()).thenReturn(List.of(spot));

        mockMvc.perform(get("/garage/spot-status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sector", is("A")))
                .andExpect(jsonPath("$[0].occupied", is(false)));
    }

    @Test
    void getSpotStatus_ShouldReturnSpotsBySector() throws Exception {
        Spot spot = new Spot();
        spot.setId(1L);
        spot.setSector("B");
        spot.setOccupied(true);

        Mockito.when(garageService.getSpots("B")).thenReturn(Collections.singletonList(spot));

        mockMvc.perform(get("/garage/spot-status")
                        .param("sector", "B"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sector", is("B")))
                .andExpect(jsonPath("$[0].occupied", is(true)));
    }

    @Test
    void getRevenue_ShouldReturnRevenue() throws Exception {
        LocalDate date = LocalDate.of(2025, 9, 29);
        Mockito.when(garageService.getRevenue("A", date)).thenReturn(15.0);

        mockMvc.perform(get("/garage/revenue")
                        .param("sector", "A")
                        .param("date", date.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("15.0"));
    }
}
