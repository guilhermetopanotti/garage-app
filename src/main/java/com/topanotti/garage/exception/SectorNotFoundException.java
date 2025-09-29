package com.topanotti.garage.exception;

public class SectorNotFoundException extends RuntimeException {
    public SectorNotFoundException(String message) {
        super(message);
    }
}
