package com.example.clinic_appointments.service;

import com.example.clinic_appointments.model.Specialty;
import com.example.clinic_appointments.repository.SpecialtyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecialtyService {

    private final SpecialtyRepository specialtyRepository;

    public SpecialtyService(SpecialtyRepository specialtyRepository) {
        this.specialtyRepository = specialtyRepository;
    }

    public List<Specialty> getAllSpecialties() {
        return specialtyRepository.findAll();
    }

    public Specialty getSpecialtyById(Long id) {
        return specialtyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Specialty not found with id " + id));
    }

    public Specialty createSpecialty(Specialty specialty) {
        return specialtyRepository.save(specialty);
    }

    public Specialty updateSpecialty(Long id, Specialty updated) {
        Specialty existing = getSpecialtyById(id);

        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());

        return specialtyRepository.save(existing);
    }

    public void deleteSpecialty(Long id) {
        if (!specialtyRepository.existsById(id)) {
            throw new RuntimeException("Specialty not found with id " + id);
        }
        specialtyRepository.deleteById(id);
    }
}
