package com.inptrental.inptrental.repository;

import com.inptrental.inptrental.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByInemailIgnoreCase(String inemail);
    Optional<Student> findByVerificationToken(String token);
    Optional<Student> findByInemail(String inemail);

}
