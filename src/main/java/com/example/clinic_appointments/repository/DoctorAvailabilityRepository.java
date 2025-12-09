package com.example.clinic_appointments.repository;

import com.example.clinic_appointments.model.Doctor;
import com.example.clinic_appointments.model.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {

    List<DoctorAvailability> findByDoctor(Doctor doctor);

    List<DoctorAvailability> findByDoctorAndDayOfWeek(Doctor doctor, DayOfWeek dayOfWeek);
}
