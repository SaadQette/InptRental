package com.inptrental.inptrental.controller;

import com.inptrental.inptrental.model.Vehicle;
import com.inptrental.inptrental.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {

    @Autowired
    private VehicleRepository vehicleRepository;

    @PostMapping
    public Vehicle addVehicle(@RequestBody Vehicle vehicle) {
        if (vehicle.getStatus() == null || vehicle.getStatus().isBlank()) {
            vehicle.setStatus("Available"); // default
        }
        return vehicleRepository.save(vehicle);
    }

    @GetMapping
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    @PatchMapping("/{vehicleId}/status")
    public String updateVehicleStatus(@PathVariable Long vehicleId, @RequestParam String status) {
        return vehicleRepository.findById(vehicleId).map(vehicle -> {
            vehicle.setStatus(status);
            vehicleRepository.save(vehicle);
            return "Vehicle status updated to " + status;
        }).orElse("Vehicle not found.");
    }
}
