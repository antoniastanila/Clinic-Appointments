package com.example.clinic_appointments.controller;

import com.example.clinic_appointments.model.Appointment;
import com.example.clinic_appointments.service.AppointmentService;
import com.example.clinic_appointments.model.AppointmentStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    // GET /api/appointments -> lista tuturor programarilor
    @GetMapping
    public List<Appointment> getAppointments(
        @RequestParam(required = false) Long patientId,
        @RequestParam(required = false) Long doctorId,
        @RequestParam(required = false) Long roomId,
        @RequestParam(required = false) AppointmentStatus status,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
) {
    if (patientId == null && doctorId == null && roomId == null && status == null && from == null && to == null) {
        return appointmentService.getAllAppointments();
    }
    return appointmentService.getFilteredAppointments(patientId, doctorId, roomId, status, from, to);
}

    // GET /api/appointments/{id} -> o programare dupa id
    @GetMapping("/{id}")
    public Appointment getAppointmentById(@PathVariable Long id) {
        return appointmentService.getAppointmentById(id);
    }

    // POST /api/appointments -> creeaza programare
    @PostMapping
    public ResponseEntity<Appointment> createAppointment(@Valid @RequestBody Appointment appointment) {
        Appointment created = appointmentService.createAppointment(appointment);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /api/appointments/{id} -> actualizeaza programare
    @PutMapping("/{id}")
    public Appointment updateAppointment(@PathVariable Long id,
                                         @Valid @RequestBody Appointment appointment) {
        return appointmentService.updateAppointment(id, appointment);
    }

    // DELETE /api/appointments/{id} -> sterge programare
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
    }
}
