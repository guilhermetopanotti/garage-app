package com.topanotti.garage.repository;

import com.topanotti.garage.model.GarageSector;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GarageSectorRepository extends JpaRepository<GarageSector, String> {

    Optional<GarageSector> findBySector(String sector);
}
