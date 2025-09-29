package com.topanotti.garage.model;

import com.topanotti.garage.model.enums.GarageEventType;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "garage_events")
public class GarageEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private GarageEventType eventType;
    private String vehiclePlate;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private String sector;
    private Double price;
}
