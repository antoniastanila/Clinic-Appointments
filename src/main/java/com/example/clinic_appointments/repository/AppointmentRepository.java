package com.example.clinic_appointments.repository;

import com.example.clinic_appointments.model.Appointment;
import com.example.clinic_appointments.model.AppointmentStatus;
import com.example.clinic_appointments.model.Doctor;
import com.example.clinic_appointments.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // BR-10 filter query
    @Query("""
    SELECT a FROM Appointment a
    WHERE (:patientId IS NULL OR a.patient.id = :patientId)
      AND (:doctorId  IS NULL OR a.doctor.id  = :doctorId)
      AND (:roomId    IS NULL OR a.room.id    = :roomId)
      AND (:status    IS NULL OR a.status     = :status)
      AND a.startTime >= COALESCE(:from, a.startTime)
      AND a.endTime   <= COALESCE(:to,   a.endTime)
    ORDER BY a.startTime DESC
""")
List<Appointment> findFiltered(
        @Param("patientId") Long patientId,
        @Param("doctorId") Long doctorId,
        @Param("roomId") Long roomId,
        @Param("status") AppointmentStatus status,
        @Param("from") LocalDateTime from,
        @Param("to") LocalDateTime to
);

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
