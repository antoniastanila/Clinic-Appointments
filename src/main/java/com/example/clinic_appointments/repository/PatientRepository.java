package com.example.clinic_appointments.repository;

import com.example.clinic_appointments.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    // aici poti adauga metode de query custom mai tarziu, de exemplu:
    // List<Patient> findByLastName(String lastName);
}
