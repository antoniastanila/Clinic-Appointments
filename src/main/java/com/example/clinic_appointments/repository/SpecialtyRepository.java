package com.example.clinic_appointments.repository;

import com.example.clinic_appointments.model.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {
    // optional mai tarziu:
    // Optional<Specialty> findByName(String name);
}
