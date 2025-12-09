package com.example.clinic_appointments.service;

import com.example.clinic_appointments.model.Doctor;
import com.example.clinic_appointments.model.DoctorAvailability;
import com.example.clinic_appointments.repository.DoctorAvailabilityRepository;
import com.example.clinic_appointments.repository.DoctorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorAvailabilityService {

    private final DoctorAvailabilityRepository availabilityRepository;
    private final DoctorRepository doctorRepository;

    public DoctorAvailabilityService(DoctorAvailabilityRepository availabilityRepository,
                                     DoctorRepository doctorRepository) {
        this.availabilityRepository = availabilityRepository;
        this.doctorRepository = doctorRepository;
    }

    public List<DoctorAvailability> getAllAvailabilities() {
        return availabilityRepository.findAll();
    }

    public DoctorAvailability getAvailabilityById(Long id) {
        return availabilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Availability not found with id " + id));
    }

    public DoctorAvailability createAvailability(DoctorAvailability availability) {
        Long doctorId = availability.getDoctor() != null ? availability.getDoctor().getId() : null;
        if (doctorId == null) {
            throw new RuntimeException("Doctor id is required");
        }

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id " + doctorId));

        availability.setDoctor(doctor);

        return availabilityRepository.save(availability);
    }

    public DoctorAvailability updateAvailability(Long id, DoctorAvailability updated) {
        DoctorAvailability existing = getAvailabilityById(id);

        existing.setDayOfWeek(updated.getDayOfWeek());
        existing.setStartTime(updated.getStartTime());
        existing.setEndTime(updated.getEndTime());

        // optional: permite si schimbarea doctorului
        if (updated.getDoctor() != null && updated.getDoctor().getId() != null) {
            Long doctorId = updated.getDoctor().getId();
            Doctor doctor = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new RuntimeException("Doctor not found with id " + doctorId));
            existing.setDoctor(doctor);
        }

        return availabilityRepository.save(existing);
    }

    public void deleteAvailability(Long id) {
        if (!availabilityRepository.existsById(id)) {
            throw new RuntimeException("Availability not found with id " + id);
        }
        availabilityRepository.deleteById(id);
    }
}
