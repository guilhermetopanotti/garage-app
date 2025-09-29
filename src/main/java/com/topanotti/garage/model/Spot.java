package com.topanotti.garage.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Spot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sector;
    private boolean occupied ;
    private String vehiclePlate;
}
