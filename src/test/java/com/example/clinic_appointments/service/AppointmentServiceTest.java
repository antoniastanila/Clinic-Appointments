package com.example.clinic_appointments.service;

import com.example.clinic_appointments.model.Appointment;
import com.example.clinic_appointments.model.AppointmentStatus;
import com.example.clinic_appointments.model.Doctor;
import com.example.clinic_appointments.model.Patient;
import com.example.clinic_appointments.repository.AppointmentRepository;
import com.example.clinic_appointments.repository.DoctorRepository;
import com.example.clinic_appointments.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    void getAllAppointments_returnsListFromRepository() {
        Appointment a1 = new Appointment();
        a1.setId(1L);
        a1.setReason("Check-up");

        Appointment a2 = new Appointment();
        a2.setId(2L);
        a2.setReason("Consultation");

        when(appointmentRepository.findAll()).thenReturn(List.of(a1, a2));

        List<Appointment> result = appointmentService.getAllAppointments();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getReason()).isEqualTo("Check-up");
        verify(appointmentRepository, times(1)).findAll();
    }

    @Test
    void getAppointmentById_existing_returnsAppointment() {
        Appointment appt = new Appointment();
        appt.setId(1L);
        appt.setReason("Check-up");

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appt));

        Appointment result = appointmentService.getAppointmentById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getReason()).isEqualTo("Check-up");
        verify(appointmentRepository).findById(1L);
    }

    @Test
    void getAppointmentById_missing_throwsException() {
        when(appointmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.getAppointmentById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Appointment not found");
    }

    @Test
    void createAppointment_valid_setsPatientDoctorAndStatusAndSaves() {
        // appointment primit "din afara", cu doar id-uri la patient/doctor
        Patient patient = new Patient();
        patient.setId(10L);

        Doctor doctor = new Doctor();
        doctor.setId(20L);

        Appointment toCreate = new Appointment();
        toCreate.setPatient(patient);
        toCreate.setDoctor(doctor);
        toCreate.setStartTime(LocalDateTime.of(2025, 1, 1, 10, 0));
        toCreate.setEndTime(LocalDateTime.of(2025, 1, 1, 11, 0));
        toCreate.setReason("Control");

        // ce exista in "baza" (mock)
        Patient persistedPatient = new Patient();
        persistedPatient.setId(10L);
        persistedPatient.setFirstName("Ana");

        Doctor persistedDoctor = new Doctor();
        persistedDoctor.setId(20L);
        persistedDoctor.setFirstName("Dr. Ion");

        when(patientRepository.findById(10L)).thenReturn(Optional.of(persistedPatient));
        when(doctorRepository.findById(20L)).thenReturn(Optional.of(persistedDoctor));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> {
            Appointment saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        Appointment created = appointmentService.createAppointment(toCreate);

        assertThat(created.getId()).isEqualTo(1L);
        assertThat(created.getPatient()).isSameAs(persistedPatient);
        assertThat(created.getDoctor()).isSameAs(persistedDoctor);
        assertThat(created.getStatus()).isEqualTo(AppointmentStatus.SCHEDULED);

        verify(patientRepository).findById(10L);
        verify(doctorRepository).findById(20L);
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void createAppointment_missingPatientOrDoctor_throwsException() {
        Appointment appt = new Appointment();
        // fara patient / fara doctor
        appt.setStartTime(LocalDateTime.now());
        appt.setEndTime(LocalDateTime.now().plusHours(1));

        assertThatThrownBy(() -> appointmentService.createAppointment(appt))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Patient and doctor must be provided");
    }

    @Test
    void createAppointment_invalidEndBeforeStart_throwsException() {
        Patient patient = new Patient();
        patient.setId(10L);

        Doctor doctor = new Doctor();
        doctor.setId(20L);

        Appointment appt = new Appointment();
        appt.setPatient(patient);
        appt.setDoctor(doctor);
        appt.setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0));
        appt.setEndTime(LocalDateTime.of(2025, 1, 1, 11, 0)); // end < start

        when(patientRepository.findById(10L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(20L)).thenReturn(Optional.of(doctor));

        assertThatThrownBy(() -> appointmentService.createAppointment(appt))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("End time cannot be before start time");
    }

    @Test
    void updateAppointment_updatesFieldsAndSaves() {
        Appointment existing = new Appointment();
        existing.setId(1L);
        existing.setReason("Old reason");
        existing.setStartTime(LocalDateTime.of(2025, 1, 1, 10, 0));
        existing.setEndTime(LocalDateTime.of(2025, 1, 1, 11, 0));
        existing.setStatus(AppointmentStatus.SCHEDULED);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(existing));

        Appointment updated = new Appointment();
        updated.setReason("New reason");
        updated.setStartTime(LocalDateTime.of(2025, 1, 2, 14, 0));
        updated.setEndTime(LocalDateTime.of(2025, 1, 2, 15, 0));
        updated.setStatus(AppointmentStatus.COMPLETED);

        when(appointmentRepository.save(existing)).thenReturn(existing);

        Appointment result = appointmentService.updateAppointment(1L, updated);

        assertThat(result.getReason()).isEqualTo("New reason");
        assertThat(result.getStartTime()).isEqualTo(LocalDateTime.of(2025, 1, 2, 14, 0));
        assertThat(result.getStatus()).isEqualTo(AppointmentStatus.COMPLETED);
        verify(appointmentRepository).save(existing);
    }

    @Test
    void deleteAppointment_existing_callsDeleteById() {
        when(appointmentRepository.existsById(1L)).thenReturn(true);

        appointmentService.deleteAppointment(1L);

        verify(appointmentRepository).deleteById(1L);
    }

    @Test
    void deleteAppointment_missing_throwsException() {
        when(appointmentRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> appointmentService.deleteAppointment(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Appointment not found");
    }
}
