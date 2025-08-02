package com.inptrental.inptrental.controller;

import com.inptrental.inptrental.dto.AvailabilityResponse;
import com.inptrental.inptrental.service.AvailabilityService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/vehicles")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @GetMapping("/{vehicleId}/availability")
    public AvailabilityResponse getAvailability(@PathVariable Long vehicleId,
                                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                 @RequestParam(defaultValue = "7") int days) {
        return availabilityService.getAvailability(vehicleId, startDate, days);
    }
}
