package com.example.clinic_appointments.controller;

import com.example.clinic_appointments.model.Patient;
import com.example.clinic_appointments.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    // GET /api/patients  -> lista tuturor pacientilor
    @GetMapping
    public List<Patient> getAllPatients() {
        return patientService.getAllPatients();
    }

    // GET /api/patients/{id} -> un pacient dupa id
    @GetMapping("/{id}")
    public Patient getPatientById(@PathVariable Long id) {
        return patientService.getPatientById(id);
    }

    // POST /api/patients -> creeaza pacient
    @PostMapping
    public ResponseEntity<Patient> createPatient(@Valid @RequestBody Patient patient) {
        Patient created = patientService.createPatient(patient);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /api/patients/{id} -> actualizeaza pacient
    @PutMapping("/{id}")
    public Patient updatePatient(@PathVariable Long id, @Valid @RequestBody Patient patient) {
        return patientService.updatePatient(id, patient);
    }

    // DELETE /api/patients/{id} -> sterge pacient
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
    }
}
