package com.inptrental.inptrental.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AvailabilityResponse {
    private Long vehicleId;
    private List<DayAvailability> availability;
}
