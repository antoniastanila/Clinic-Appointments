package com.example.clinic_appointments.controller;

import com.example.clinic_appointments.model.DoctorAvailability;
import com.example.clinic_appointments.service.DoctorAvailabilityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor-availabilities")
public class DoctorAvailabilityController {

    private final DoctorAvailabilityService availabilityService;

    public DoctorAvailabilityController(DoctorAvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    // GET /api/doctor-availabilities
    @GetMapping
    public List<DoctorAvailability> getAllAvailabilities() {
        return availabilityService.getAllAvailabilities();
    }

    // GET /api/doctor-availabilities/{id}
    @GetMapping("/{id}")
    public DoctorAvailability getAvailabilityById(@PathVariable Long id) {
        return availabilityService.getAvailabilityById(id);
    }

    // POST /api/doctor-availabilities
    @PostMapping
    public ResponseEntity<DoctorAvailability> createAvailability(
            @Valid @RequestBody DoctorAvailability availability) {
        DoctorAvailability created = availabilityService.createAvailability(availability);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /api/doctor-availabilities/{id}
    @PutMapping("/{id}")
    public DoctorAvailability updateAvailability(
            @PathVariable Long id,
            @Valid @RequestBody DoctorAvailability availability) {
        return availabilityService.updateAvailability(id, availability);
    }

    // DELETE /api/doctor-availabilities/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAvailability(@PathVariable Long id) {
        availabilityService.deleteAvailability(id);
    }
}
