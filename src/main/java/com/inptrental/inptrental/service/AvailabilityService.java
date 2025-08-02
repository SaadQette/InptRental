package com.inptrental.inptrental.service;

import com.inptrental.inptrental.dto.AvailabilityResponse;
import com.inptrental.inptrental.dto.DayAvailability;
import com.inptrental.inptrental.model.Reservation;
import com.inptrental.inptrental.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

@Service
public class AvailabilityService {

    private final ReservationRepository reservationRepository;

    public AvailabilityService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public AvailabilityResponse getAvailability(Long vehicleId, LocalDate startDate, int days) {
        List<DayAvailability> result = new ArrayList<>();
        ZoneOffset zone = ZoneOffset.UTC;
        for (int i = 0; i < days; i++) {
            LocalDate current = startDate.plusDays(i);
            OffsetDateTime dayStart = current.atStartOfDay().atOffset(zone);
            OffsetDateTime dayEnd = dayStart.plusDays(1);

            List<Reservation> reservations = reservationRepository.findOverlappingReservations(vehicleId, dayStart, dayEnd);
            Set<Integer> booked = new HashSet<>();

            for (Reservation res : reservations) {
                OffsetDateTime start = res.getStartTime().isBefore(dayStart) ? dayStart : res.getStartTime();
                OffsetDateTime end = res.getEndTime().isAfter(dayEnd) ? dayEnd : res.getEndTime();

                int startHour = start.getHour();
                int endHour = end.minusSeconds(1).getHour();
                for (int h = startHour; h <= endHour; h++) {
                    booked.add(h);
                }
            }
            List<Integer> bookedHours = new ArrayList<>(booked);
            Collections.sort(bookedHours);
            result.add(new DayAvailability(current, bookedHours));
        }
        return new AvailabilityResponse(vehicleId, result);
    }
}
