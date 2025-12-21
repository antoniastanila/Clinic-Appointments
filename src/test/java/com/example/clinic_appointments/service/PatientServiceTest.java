package com.example.clinic_appointments.service;

import com.example.clinic_appointments.model.Patient;
import com.example.clinic_appointments.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    @Test
    void getAllPatients_returnsListFromRepository() {
        Patient p1 = new Patient();
        p1.setId(1L);
        p1.setFirstName("Ana");

        Patient p2 = new Patient();
        p2.setId(2L);
        p2.setFirstName("Ion");

        when(patientRepository.findAll()).thenReturn(List.of(p1, p2));

        List<Patient> result = patientService.getAllPatients();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getFirstName()).isEqualTo("Ana");
        verify(patientRepository, times(1)).findAll();
    }

    @Test
    void getPatientById_existing_returnsPatient() {
        Patient p = new Patient();
        p.setId(1L);
        p.setFirstName("Ana");

        when(patientRepository.findById(1L)).thenReturn(Optional.of(p));

        Patient result = patientService.getPatientById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFirstName()).isEqualTo("Ana");
        verify(patientRepository).findById(1L);
    }

    @Test
    void getPatientById_missing_throwsException() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.getPatientById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Patient not found");
    }

    @Test
    void createPatient_savesToRepository() {
        Patient p = new Patient();
        p.setFirstName("Ana");
        p.setLastName("Popescu");
        p.setEmail("ana@example.com");
        p.setPhone("0712345678");
        p.setDateOfBirth(LocalDate.of(1990, 1, 1));

        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        Patient created = patientService.createPatient(p);

        assertThat(created.getId()).isEqualTo(1L);

        ArgumentCaptor<Patient> captor = ArgumentCaptor.forClass(Patient.class);
        verify(patientRepository).save(captor.capture());
        assertThat(captor.getValue().getFirstName()).isEqualTo("Ana");
    }

    @Test
    void updatePatient_updatesFieldsAndSaves() {
        // existing patient in "database"
        Patient existing = new Patient();
        existing.setId(1L);
        existing.setFirstName("Old");
        existing.setLastName("Name");
        existing.setEmail("old@example.com");

        when(patientRepository.findById(1L)).thenReturn(Optional.of(existing));

        // new data from update request
        Patient updated = new Patient();
        updated.setFirstName("New");
        updated.setLastName("Name");
        updated.setEmail("new@example.com");

        // call service
        patientService.updatePatient(1L, updated);

        // verify fields updated
        assertThat(existing.getFirstName()).isEqualTo("New");
        assertThat(existing.getEmail()).isEqualTo("new@example.com");

        // verify save called
        verify(patientRepository).save(existing);
    }

    @Test
    void deletePatient_existing_callsDeleteById() {
        when(patientRepository.existsById(1L)).thenReturn(true);

        patientService.deletePatient(1L);

        verify(patientRepository).deleteById(1L);
    }

    @Test
    void deletePatient_missing_throwsException() {
        when(patientRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> patientService.deletePatient(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Patient not found");
    }
}
