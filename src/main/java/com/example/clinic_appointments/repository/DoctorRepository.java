package com.example.clinic_appointments.repository;

import com.example.clinic_appointments.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    // aici putem adauga metode custom mai tarziu, de ex:
    // List<Doctor> findBySpecialization(String specialization);
}
