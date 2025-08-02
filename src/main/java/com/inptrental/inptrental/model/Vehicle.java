package com.inptrental.inptrental.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String type; // "Scooter" or "Velo"
    private String status; // "Available" or "Rented" or "Unavailable"

    private String imageUrl;
}
