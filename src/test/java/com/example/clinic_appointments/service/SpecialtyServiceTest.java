package com.example.clinic_appointments.service;

import com.example.clinic_appointments.model.Specialty;
import com.example.clinic_appointments.repository.SpecialtyRepository;
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
class SpecialtyServiceTest {

    @Mock
    private SpecialtyRepository specialtyRepository;

    @InjectMocks
    private SpecialtyService specialtyService;

    @Test
    void getAllSpecialties_returnsListFromRepository() {
        Specialty s1 = new Specialty();
        s1.setId(1L);
        s1.setName("Cardiology");
        s1.setDescription("Heart related");

        Specialty s2 = new Specialty();
        s2.setId(2L);
        s2.setName("Dermatology");
        s2.setDescription("Skin related");

        when(specialtyRepository.findAll()).thenReturn(List.of(s1, s2));

        List<Specialty> result = specialtyService.getAllSpecialties();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Cardiology");
        verify(specialtyRepository, times(1)).findAll();
    }

    @Test
    void getSpecialtyById_existing_returnsSpecialty() {
        Specialty s = new Specialty();
        s.setId(1L);
        s.setName("Cardiology");
        s.setDescription("Heart related");

        when(specialtyRepository.findById(1L)).thenReturn(Optional.of(s));

        Specialty result = specialtyService.getSpecialtyById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Cardiology");
        verify(specialtyRepository).findById(1L);
    }

    @Test
    void getSpecialtyById_missing_throwsException() {
        when(specialtyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.getSpecialtyById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Specialty not found");
    }

    @Test
    void createSpecialty_savesToRepository() {
        Specialty s = new Specialty();
        s.setName("Cardiology");
        s.setDescription("Heart related");

        when(specialtyRepository.save(any(Specialty.class))).thenAnswer(invocation -> {
            Specialty saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        Specialty created = specialtyService.createSpecialty(s);

        assertThat(created.getId()).isEqualTo(1L);
        verify(specialtyRepository).save(any(Specialty.class));
    }

    @Test
    void updateSpecialty_updatesFieldsAndSaves() {
        Specialty existing = new Specialty();
        existing.setId(1L);
        existing.setName("Old name");
        existing.setDescription("Old desc");

        when(specialtyRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(specialtyRepository.save(existing)).thenReturn(existing);

        Specialty updated = new Specialty();
        updated.setName("New name");
        updated.setDescription("New desc");

        Specialty result = specialtyService.updateSpecialty(1L, updated);

        assertThat(result.getName()).isEqualTo("New name");
        assertThat(result.getDescription()).isEqualTo("New desc");
        verify(specialtyRepository).save(existing);
    }

    @Test
    void deleteSpecialty_existing_callsDeleteById() {
        when(specialtyRepository.existsById(1L)).thenReturn(true);

        specialtyService.deleteSpecialty(1L);

        verify(specialtyRepository).deleteById(1L);
    }

    @Test
    void deleteSpecialty_missing_throwsException() {
        when(specialtyRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> specialtyService.deleteSpecialty(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Specialty not found");
    }
}
