package com.example.clinic_appointments.repository;

import com.example.clinic_appointments.model.Appointment;
import com.example.clinic_appointments.model.Doctor;
import com.example.clinic_appointments.model.Patient;
import com.example.clinic_appointments.model.AppointmentStatus;
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
     boolean existsByDoctor_IdAndStatusNotAndStartTimeLessThanAndEndTimeGreaterThan(
            Long doctorId,
            AppointmentStatus excludedStatus,
            LocalDateTime end,
            LocalDateTime start
    );

    boolean existsByRoom_IdAndStatusNotAndStartTimeLessThanAndEndTimeGreaterThan(
            Long roomId,
            AppointmentStatus excludedStatus,
            LocalDateTime end,
            LocalDateTime start
    );

    // pentru update (exclude programarea curentă)
    boolean existsByDoctor_IdAndStatusNotAndIdNotAndStartTimeLessThanAndEndTimeGreaterThan(
            Long doctorId,
            AppointmentStatus excludedStatus,
            Long excludeId,
            LocalDateTime end,
            LocalDateTime start
    );

    boolean existsByRoom_IdAndStatusNotAndIdNotAndStartTimeLessThanAndEndTimeGreaterThan(
            Long roomId,
            AppointmentStatus excludedStatus,
            Long excludeId,
            LocalDateTime end,
            LocalDateTime start
    );                                                
}
