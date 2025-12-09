package com.example.clinic_appointments.repository;

import com.example.clinic_appointments.model.Appointment;
import com.example.clinic_appointments.model.Doctor;
import com.example.clinic_appointments.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatient(Patient patient);

    List<Appointment> findByDoctor(Doctor doctor);

    List<Appointment> findByDoctorAndStartTimeBetween(Doctor doctor,
                                                      LocalDateTime start,
                                                      LocalDateTime end);
}
