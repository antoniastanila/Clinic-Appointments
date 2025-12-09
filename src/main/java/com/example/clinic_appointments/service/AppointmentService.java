package com.example.clinic_appointments.service;

import com.example.clinic_appointments.model.Appointment;
import com.example.clinic_appointments.model.AppointmentStatus;
import com.example.clinic_appointments.model.Doctor;
import com.example.clinic_appointments.model.Patient;
import com.example.clinic_appointments.repository.AppointmentRepository;
import com.example.clinic_appointments.repository.DoctorRepository;
import com.example.clinic_appointments.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id " + id));
    }

    public Appointment createAppointment(Appointment appointment) {
        // verificam ca pacientul si doctorul exista
        Long patientId = appointment.getPatient() != null ? appointment.getPatient().getId() : null;
        Long doctorId = appointment.getDoctor() != null ? appointment.getDoctor().getId() : null;

        if (patientId == null || doctorId == null) {
            throw new RuntimeException("Patient and doctor must be provided with valid IDs");
        }

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found with id " + patientId));
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id " + doctorId));

        appointment.setPatient(patient);
        appointment.setDoctor(doctor);

        // optional: verificare simpla sa nu fie endTime inainte de startTime
        LocalDateTime start = appointment.getStartTime();
        LocalDateTime end = appointment.getEndTime();
        if (start != null && end != null && end.isBefore(start)) {
            throw new RuntimeException("End time cannot be before start time");
        }

        // status default daca nu e setat
        if (appointment.getStatus() == null) {
            appointment.setStatus(AppointmentStatus.SCHEDULED);
        }

        return appointmentRepository.save(appointment);
    }

    public Appointment updateAppointment(Long id, Appointment updated) {
        Appointment existing = getAppointmentById(id);

        // daca vin noi patient/doctor in updated, ii putem ignora sau procesa;
        // pentru inceput ne concentram pe timp, reason si status
        existing.setStartTime(updated.getStartTime());
        existing.setEndTime(updated.getEndTime());
        existing.setReason(updated.getReason());
        if (updated.getStatus() != null) {
            existing.setStatus(updated.getStatus());
        }

        return appointmentRepository.save(existing);
    }

    public void deleteAppointment(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new RuntimeException("Appointment not found with id " + id);
        }
        appointmentRepository.deleteById(id);
    }
}
