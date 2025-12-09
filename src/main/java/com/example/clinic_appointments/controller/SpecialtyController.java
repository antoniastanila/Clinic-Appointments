package com.example.clinic_appointments.controller;

import com.example.clinic_appointments.model.Specialty;
import com.example.clinic_appointments.service.SpecialtyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/specialties")
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    public SpecialtyController(SpecialtyService specialtyService) {
        this.specialtyService = specialtyService;
    }

    // GET /api/specialties -> lista tuturor specializarilor
    @GetMapping
    public List<Specialty> getAllSpecialties() {
        return specialtyService.getAllSpecialties();
    }

    // GET /api/specialties/{id} -> o specializare dupa id
    @GetMapping("/{id}")
    public Specialty getSpecialtyById(@PathVariable Long id) {
        return specialtyService.getSpecialtyById(id);
    }

    // POST /api/specialties -> creeaza specializare
    @PostMapping
    public ResponseEntity<Specialty> createSpecialty(@Valid @RequestBody Specialty specialty) {
        Specialty created = specialtyService.createSpecialty(specialty);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /api/specialties/{id} -> actualizeaza specializare
    @PutMapping("/{id}")
    public Specialty updateSpecialty(@PathVariable Long id,
                                     @Valid @RequestBody Specialty specialty) {
        return specialtyService.updateSpecialty(id, specialty);
    }

    // DELETE /api/specialties/{id} -> sterge specializare
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSpecialty(@PathVariable Long id) {
        specialtyService.deleteSpecialty(id);
    }
}
