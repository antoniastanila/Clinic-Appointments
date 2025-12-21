package com.example.clinic_appointments.service;

import com.example.clinic_appointments.model.Doctor;
import com.example.clinic_appointments.model.DoctorAvailability;
import com.example.clinic_appointments.repository.DoctorAvailabilityRepository;
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
class DoctorAvailabilityServiceTest {

    @Mock
    private DoctorAvailabilityRepository availabilityRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorAvailabilityService availabilityService;

    @Test
    void getAllAvailabilities_returnsListFromRepository() {
        DoctorAvailability a1 = new DoctorAvailability();
        a1.setId(1L);

        DoctorAvailability a2 = new DoctorAvailability();
        a2.setId(2L);

        when(availabilityRepository.findAll()).thenReturn(List.of(a1, a2));

        List<DoctorAvailability> result = availabilityService.getAllAvailabilities();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        verify(availabilityRepository, times(1)).findAll();
    }

    @Test
    void getAvailabilityById_existing_returnsAvailability() {
        DoctorAvailability availability = new DoctorAvailability();
        availability.setId(1L);

        when(availabilityRepository.findById(1L)).thenReturn(Optional.of(availability));

        DoctorAvailability result = availabilityService.getAvailabilityById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        verify(availabilityRepository).findById(1L);
    }

    @Test
    void getAvailabilityById_missing_throwsException() {
        when(availabilityRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> availabilityService.getAvailabilityById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Availability not found");
    }

    @Test
    void createAvailability_valid_setsDoctorAndSaves() {
        // availability primit din afară, cu doar id la doctor
        Doctor docRef = new Doctor();
        docRef.setId(10L);

        DoctorAvailability toCreate = new DoctorAvailability();
        toCreate.setDoctor(docRef);

        // doctorul existent în "bază"
        Doctor persistedDoctor = new Doctor();
        persistedDoctor.setId(10L);
        persistedDoctor.setFirstName("Dr. Ana");

        when(doctorRepository.findById(10L)).thenReturn(Optional.of(persistedDoctor));
        when(availabilityRepository.save(any(DoctorAvailability.class))).thenAnswer(invocation -> {
            DoctorAvailability saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        DoctorAvailability created = availabilityService.createAvailability(toCreate);

        assertThat(created.getId()).isEqualTo(1L);
        assertThat(created.getDoctor()).isSameAs(persistedDoctor);

        verify(doctorRepository).findById(10L);
        verify(availabilityRepository).save(any(DoctorAvailability.class));
    }

    @Test
    void createAvailability_missingDoctorId_throwsException() {
        DoctorAvailability availability = new DoctorAvailability();
        // fără doctor sau fără id

        assertThatThrownBy(() -> availabilityService.createAvailability(availability))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Doctor id is required");
    }

    @Test
    void updateAvailability_updatesFieldsAndDoctorAndSaves() {
        DoctorAvailability existing = new DoctorAvailability();
        existing.setId(1L);

        Doctor oldDoctor = new Doctor();
        oldDoctor.setId(10L);
        existing.setDoctor(oldDoctor);

        when(availabilityRepository.findById(1L)).thenReturn(Optional.of(existing));

        // updated cu doctor nou
        Doctor newDoctorRef = new Doctor();
        newDoctorRef.setId(20L);

        DoctorAvailability updated = new DoctorAvailability();
        updated.setDoctor(newDoctorRef);
        // nu ne bazăm pe tipul exact al dayOfWeek/start/end, le putem lăsa null sau copia
        updated.setDayOfWeek(existing.getDayOfWeek());
        updated.setStartTime(existing.getStartTime());
        updated.setEndTime(existing.getEndTime());

        Doctor newDoctor = new Doctor();
        newDoctor.setId(20L);
        newDoctor.setFirstName("Dr. Nou");

        when(doctorRepository.findById(20L)).thenReturn(Optional.of(newDoctor));
        when(availabilityRepository.save(existing)).thenReturn(existing);

        DoctorAvailability result = availabilityService.updateAvailability(1L, updated);

        assertThat(result.getDoctor()).isSameAs(newDoctor);
        verify(doctorRepository).findById(20L);
        verify(availabilityRepository).save(existing);
    }

    @Test
    void deleteAvailability_existing_callsDeleteById() {
        when(availabilityRepository.existsById(1L)).thenReturn(true);

        availabilityService.deleteAvailability(1L);

        verify(availabilityRepository).deleteById(1L);
    }

    @Test
    void deleteAvailability_missing_throwsException() {
        when(availabilityRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> availabilityService.deleteAvailability(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Availability not found");
    }
}
