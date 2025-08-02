package com.inptrental.inptrental.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class DayAvailability {
    private LocalDate date;
    private List<Integer> bookedHours;
}
