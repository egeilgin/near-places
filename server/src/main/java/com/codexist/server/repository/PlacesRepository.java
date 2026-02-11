package com.codexist.server.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codexist.server.entity.PlacesEntity;

@Repository
public interface PlacesRepository extends JpaRepository<PlacesEntity, Long> {
    Optional<PlacesEntity> findByLatitudeAndLongitudeAndRadiusAndAmenity(
        double latitude,
        double logitude,
        int radius,
        String amenity
    );
}
