package com.topanotti.garage.exception;

public class VehicleAlreadyParkedException extends RuntimeException {
    public VehicleAlreadyParkedException(String message) {
        super(message);
    }
}