package com.topanotti.garage.dto;

import com.topanotti.garage.model.enums.GarageEventType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VehichleDTO {
    private String vehiclePlate;
    private GarageEventType eventType;
}
