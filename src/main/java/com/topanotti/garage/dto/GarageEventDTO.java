package com.topanotti.garage.dto;

import com.topanotti.garage.model.GarageSector;
import com.topanotti.garage.model.enums.GarageEventType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class GarageEventDTO {

    private String vehiclePlate;
    private GarageEventType eventType;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private String sector;
}
