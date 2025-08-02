package com.inptrental.inptrental.model;

import jakarta.persistence.*;
import lombok.Data;


import java.time.LocalDateTime;

@Entity
@Data
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Student student;

    @ManyToOne
    private Vehicle vehicle;

    private LocalDateTime rentalStartTime;
    private LocalDateTime rentalEndTime;

    private Boolean payed = false ;

}
