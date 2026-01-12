package com.example.clinic_appointments.service;
import com.example.clinic_appointments.exception.BadRequestException;
import com.example.clinic_appointments.exception.ConflictException;
import com.example.clinic_appointments.exception.ResourceNotFoundException;

import com.example.clinic_appointments.model.Appointment;
import com.example.clinic_appointments.model.AppointmentStatus;
import com.example.clinic_appointments.model.Doctor;
import com.example.clinic_appointments.model.Patient;
import com.example.clinic_appointments.model.Room;
import com.example.clinic_appointments.repository.AppointmentRepository;
import com.example.clinic_appointments.repository.DoctorRepository;
import com.example.clinic_appointments.repository.PatientRepository;
import com.example.clinic_appointments.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final RoomRepository roomRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository,
                              RoomRepository roomRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.roomRepository = roomRepository;
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id " + id));
    }

    @Transactional
public Appointment createAppointment(Appointment appointment) {

    if (appointment == null) {
        throw new BadRequestException("Request body is missing");
    }

    Long patientId = (appointment.getPatient() != null) ? appointment.getPatient().getId() : null;
    Long doctorId  = (appointment.getDoctor()  != null) ? appointment.getDoctor().getId()  : null;
    Long roomId    = (appointment.getRoom()    != null) ? appointment.getRoom().getId()    : null;

    if (patientId == null || doctorId == null || roomId == null) {
        throw new BadRequestException("Patient, doctor and room must be provided with valid IDs");
    }

    LocalDateTime start = appointment.getStartTime();
    LocalDateTime end   = appointment.getEndTime();

    if (start == null || end == null) {
        throw new BadRequestException("Start time and end time are required");
    }
    if (!end.isAfter(start)) {
        throw new BadRequestException("End time must be after start time");
    }

    // set default status
    if (appointment.getStatus() == null) {
        appointment.setStatus(AppointmentStatus.SCHEDULED);
    }

    // overlap checks (ignoring CANCELLED)
    boolean doctorOverlap =
            appointmentRepository.existsByDoctor_IdAndStatusNotAndStartTimeLessThanAndEndTimeGreaterThan(
                    doctorId, AppointmentStatus.CANCELLED, end, start);

    if (doctorOverlap) {
        throw new ConflictException("Overlapping appointment for the same doctor");
    }

    boolean roomOverlap =
            appointmentRepository.existsByRoom_IdAndStatusNotAndStartTimeLessThanAndEndTimeGreaterThan(
                    roomId, AppointmentStatus.CANCELLED, end, start);

    if (roomOverlap) {
        throw new ConflictException("Overlapping appointment for the same room");
    }

    // fetch entities only after we know the slot is valid
    Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id " + patientId));

    Doctor doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id " + doctorId));

    Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new ResourceNotFoundException("Room not found with id " + roomId));

    appointment.setPatient(patient);
    appointment.setDoctor(doctor);
    appointment.setRoom(room);

    return appointmentRepository.save(appointment);
}
    @Transactional
public Appointment updateAppointment(Long id, Appointment updated) {
    Appointment existing = getAppointmentById(id);

    LocalDateTime start = updated.getStartTime();
    LocalDateTime end   = updated.getEndTime();

    if (start == null || end == null) {
        throw new RuntimeException("Start time and end time are required");
    }
    if (!end.isAfter(start)) {
        throw new RuntimeException("End time must be after start time");
    }

    Long doctorId = existing.getDoctor().getId();
    Long roomId   = existing.getRoom().getId();

    // dacă permiți schimbarea doctor/cameră la update, aici trebuie să le citești din updated
    // și să le validezi + setezi, apoi verifici overlap cu noile ID-uri

    if (appointmentRepository.existsByDoctor_IdAndStatusNotAndIdNotAndStartTimeLessThanAndEndTimeGreaterThan(
            doctorId, AppointmentStatus.CANCELLED, id, end, start)) {
        throw new RuntimeException("Overlapping appointment for the same doctor");
    }

    if (appointmentRepository.existsByRoom_IdAndStatusNotAndIdNotAndStartTimeLessThanAndEndTimeGreaterThan(
            roomId, AppointmentStatus.CANCELLED, id, end, start)) {
        throw new RuntimeException("Overlapping appointment for the same room");
    }

    existing.setStartTime(start);
    existing.setEndTime(end);
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

    public List<Appointment> getFilteredAppointments(Long patientId,
                                                Long doctorId,
                                                Long roomId,
                                                AppointmentStatus status,
                                                LocalDateTime from,
                                                LocalDateTime to) {
    return appointmentRepository.findFiltered(patientId, doctorId, roomId, status, from, to);
}

}
