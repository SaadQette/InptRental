package com.inptrental.inptrental.repository;

import com.inptrental.inptrental.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r WHERE r.vehicle.id = :vehicleId AND r.startTime < :end AND r.endTime > :start")
    List<Reservation> findOverlappingReservations(@Param("vehicleId") Long vehicleId,
                                                  @Param("start") OffsetDateTime start,
                                                  @Param("end") OffsetDateTime end);
}
