package com.codexist.server.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;



@Entity
@Table(
    name = "place_results",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_place_cache_key",
        columnNames = {"latitude", "longitude", "radius", "amenity"}
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlacesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Rounded to 6 decimal places for stable cache keys */
    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    /** Search radius in metres */
    @Column(nullable = false)
    private int radius;

    @Column(nullable = false, length = 64)
    private String amenity;

    /** Raw JSON response from Overpass API */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String responseJson;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
