package com.topanotti.garage.config;

import com.topanotti.garage.model.GarageSector;
import com.topanotti.garage.model.Spot;
import com.topanotti.garage.repository.GarageSectorRepository;
import com.topanotti.garage.repository.SpotRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    ApplicationRunner initData(GarageSectorRepository sectorRepository,
                               SpotRepository spotRepository) {
        return args -> {
            if (sectorRepository.count() == 0) {
                GarageSector sectorA = new GarageSector();
                sectorA.setSector("A");
                sectorA.setBasePrice(1.0);
                sectorA.setMaxCapacity(10);
                sectorRepository.save(sectorA);

                for (int i = 1; i <= sectorA.getMaxCapacity(); i++) {
                    Spot spot = new Spot();
                    spot.setSector(sectorA.getSector());
                    spot.setOccupied(false);
                    spot.setVehiclePlate(null);
                    spotRepository.save(spot);
                }

                GarageSector sectorB = new GarageSector();
                sectorB.setSector("B");
                sectorB.setBasePrice(2.0);
                sectorB.setMaxCapacity(10);
                sectorRepository.save(sectorB);

                for (int i = 1; i <= sectorB.getMaxCapacity(); i++) {
                    Spot spot = new Spot();
                    spot.setSector(sectorB.getSector());
                    spot.setOccupied(false);
                    spot.setVehiclePlate(null);
                    spotRepository.save(spot);
                }
            }
        };
    }
}

