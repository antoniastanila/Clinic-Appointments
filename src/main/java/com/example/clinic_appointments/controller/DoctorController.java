package com.example.clinic_appointments.controller;

import com.example.clinic_appointments.model.Doctor;
import com.example.clinic_appointments.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    // GET /api/doctors -> lista tuturor doctorilor
    @GetMapping
    public List<Doctor> getAllDoctors() {
        return doctorService.getAllDoctors();
    }

    // GET /api/doctors/{id} -> un doctor dupa id
    @GetMapping("/{id}")
    public Doctor getDoctorById(@PathVariable Long id) {
        return doctorService.getDoctorById(id);
    }

    // POST /api/doctors -> creeaza doctor
    @PostMapping
    public ResponseEntity<Doctor> createDoctor(@Valid @RequestBody Doctor doctor) {
        Doctor created = doctorService.createDoctor(doctor);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /api/doctors/{id} -> actualizeaza doctor
    @PutMapping("/{id}")
    public Doctor updateDoctor(@PathVariable Long id, @Valid @RequestBody Doctor doctor) {
        return doctorService.updateDoctor(id, doctor);
    }

    // DELETE /api/doctors/{id} -> sterge doctor
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
    }
}
