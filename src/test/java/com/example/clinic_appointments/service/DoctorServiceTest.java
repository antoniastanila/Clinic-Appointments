package com.example.clinic_appointments.service;

import com.example.clinic_appointments.model.Doctor;
import com.example.clinic_appointments.repository.DoctorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorService doctorService;

    @Test
    void getAllDoctors_returnsListFromRepository() {
        Doctor d1 = new Doctor();
        d1.setId(1L);
        d1.setFirstName("Ana");

        Doctor d2 = new Doctor();
        d2.setId(2L);
        d2.setFirstName("Ion");

        when(doctorRepository.findAll()).thenReturn(List.of(d1, d2));

        List<Doctor> result = doctorService.getAllDoctors();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getFirstName()).isEqualTo("Ana");
        verify(doctorRepository, times(1)).findAll();
    }

    @Test
    void getDoctorById_existing_returnsDoctor() {
        Doctor d = new Doctor();
        d.setId(1L);
        d.setFirstName("Ana");

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(d));

        Doctor result = doctorService.getDoctorById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFirstName()).isEqualTo("Ana");
        verify(doctorRepository).findById(1L);
    }

    @Test
    void getDoctorById_missing_throwsException() {
        when(doctorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> doctorService.getDoctorById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Doctor not found");
    }

    @Test
    void createDoctor_savesToRepository() {
        Doctor d = new Doctor();
        d.setFirstName("Ana");
        d.setLastName("Popescu");
        d.setEmail("ana@example.com");
        d.setPhone("0712345678");
        d.setSpecialization("Cardiology");

        when(doctorRepository.save(any(Doctor.class))).thenAnswer(invocation -> {
            Doctor saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        Doctor created = doctorService.createDoctor(d);

        assertThat(created.getId()).isEqualTo(1L);
        verify(doctorRepository).save(any(Doctor.class));
    }

    @Test
    void updateDoctor_updatesFieldsAndSaves() {
        Doctor existing = new Doctor();
        existing.setId(1L);
        existing.setFirstName("Old");
        existing.setLastName("Name");
        existing.setEmail("old@example.com");
        existing.setPhone("000");
        existing.setSpecialization("OldSpec");

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(doctorRepository.save(existing)).thenReturn(existing);

        Doctor updated = new Doctor();
        updated.setFirstName("New");
        updated.setLastName("Name");
        updated.setEmail("new@example.com");
        updated.setPhone("111");
        updated.setSpecialization("NewSpec");

        Doctor result = doctorService.updateDoctor(1L, updated);

        assertThat(result.getFirstName()).isEqualTo("New");
        assertThat(result.getEmail()).isEqualTo("new@example.com");
        assertThat(result.getSpecialization()).isEqualTo("NewSpec");
        verify(doctorRepository).save(existing);
    }

    @Test
    void deleteDoctor_existing_callsDeleteById() {
        when(doctorRepository.existsById(1L)).thenReturn(true);

        doctorService.deleteDoctor(1L);

        verify(doctorRepository).deleteById(1L);
    }

    @Test
    void deleteDoctor_missing_throwsException() {
        when(doctorRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> doctorService.deleteDoctor(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Doctor not found");
    }
}
