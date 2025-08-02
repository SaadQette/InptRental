package com.inptrental.inptrental.repository;

import com.inptrental.inptrental.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    // Find rentals by student ID
    List<Rental> findByStudentId(Long studentId);

    // Count rentals by student ID
    int countByStudentId(Long studentId);

    // Find rentals by vehicle ID and overlapping time range
    List<Rental> findByVehicleIdAndRentalEndTimeAfterAndRentalStartTimeBefore(Long vehicleId,
                                                                              java.time.LocalDateTime start,
                                                                              java.time.LocalDateTime end);

    List<Rental> findByPayedFalse();

    @Query("""
    SELECT r FROM Rental r
    WHERE r.payed = false
      AND r.rentalStartTime = (
          SELECT MIN(r2.rentalStartTime)
          FROM Rental r2
          WHERE r2.student.id = r.student.id
      )
    ORDER BY r.rentalStartTime DESC
""")
    List<Rental> findFirstUnpayedRentalsPerStudent();
}
