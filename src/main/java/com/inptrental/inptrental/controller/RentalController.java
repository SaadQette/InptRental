package com.inptrental.inptrental.controller;

import com.inptrental.inptrental.model.Rental;
import com.inptrental.inptrental.repository.RentalRepository;
import com.inptrental.inptrental.repository.StudentRepository;
import com.inptrental.inptrental.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/rentals")
public class RentalController {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @PostMapping
    public String createRental(@RequestBody Rental rental) {
        // time window enforcement
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime start = rental.getRentalStartTime();
        LocalDateTime end = rental.getRentalEndTime();

        if (start == null || end == null) {
            return "Start and end times are required.";
        }

        if (start.isBefore(now)) {
            return "Cannot book in the past.";
        }
        if (start.isAfter(now.plusDays(7))) {
            return "Start time must be within the next 7 days.";
        }
        if (end.isBefore(start.plusMinutes(30))) {
            return "Rental must be at least 30 minutes.";
        }
        if (start.getMinute() % 30 != 0) {
            return "Start minutes must be 00 or 30.";
        }
        if (end.getMinute() % 30 != 0) {
            return "End minutes must be 00 or 30.";
        }

        if (rental.getStudent() == null || rental.getStudent().getId() == null) {
            return "Student information is missing.";
        }
        if (rental.getVehicle() == null || rental.getVehicle().getId() == null) {
            return "Vehicle information is missing.";
        }

        var vehicle = vehicleRepository.findById(rental.getVehicle().getId()).orElse(null);
        if (vehicle == null) {
            return "Vehicle not found.";
        }

        if ("Unavailable".equalsIgnoreCase(vehicle.getStatus())) {
            return "Vehicle is currently unavailable for repairs.";
        }

        // Availability check (overlap)
        List<Rental> overlappingRentals = rentalRepository
                .findByVehicleIdAndRentalEndTimeAfterAndRentalStartTimeBefore(
                        rental.getVehicle().getId(),
                        rental.getRentalStartTime(),
                        rental.getRentalEndTime());

        if (!overlappingRentals.isEmpty()) {
            return "Vehicle is already rented in this time period.";
        }

        int rentalCount = rentalRepository.countByStudentId(rental.getStudent().getId());

        rental.setPayed(false); // default
        rentalRepository.save(rental);

        if (rentalCount == 0) {
            return "Rental booked successfully. Please ensure the student signs the safety form.";
        }

        return "Rental booked successfully.";
    }

    @GetMapping
    public List<Rental> getAllRentals() {
        return rentalRepository.findAll();
    }

    @GetMapping("/student/{studentId}")
    public List<Rental> getRentalHistory(@PathVariable Long studentId) {
        return rentalRepository.findByStudentId(studentId);
    }

    @PatchMapping("/{rentalId}/mark-payed")
    public String markRentalAsPayed(@PathVariable Long rentalId) {
        return rentalRepository.findById(rentalId).map(rental -> {
            rental.setPayed(true);
            rentalRepository.save(rental);
            return "Rental marked as payed.";
        }).orElse("Rental not found.");
    }

    @GetMapping("/unpayed")
    public List<Rental> getUnpayedRentals() {
        return rentalRepository.findByPayedFalse();
    }

    @GetMapping("/first-rental-unpayed")
    public List<Rental> getFirstUnpayedRentalsPerStudent() {
        return rentalRepository.findFirstUnpayedRentalsPerStudent();
    }

}
