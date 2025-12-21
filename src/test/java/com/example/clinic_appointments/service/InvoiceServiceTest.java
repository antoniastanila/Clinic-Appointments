package com.example.clinic_appointments.service;

import com.example.clinic_appointments.model.Appointment;
import com.example.clinic_appointments.model.Invoice;
import com.example.clinic_appointments.model.InvoiceStatus;
import com.example.clinic_appointments.model.Patient;
import com.example.clinic_appointments.repository.AppointmentRepository;
import com.example.clinic_appointments.repository.InvoiceRepository;
import com.example.clinic_appointments.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private InvoiceService invoiceService;

    @Test
    void getAllInvoices_returnsListFromRepository() {
        Invoice i1 = new Invoice();
        i1.setId(1L);
        i1.setAmount(BigDecimal.TEN);

        Invoice i2 = new Invoice();
        i2.setId(2L);
        i2.setAmount(BigDecimal.valueOf(20));

        when(invoiceRepository.findAll()).thenReturn(List.of(i1, i2));

        List<Invoice> result = invoiceService.getAllInvoices();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        verify(invoiceRepository, times(1)).findAll();
    }

    @Test
    void getInvoiceById_existing_returnsInvoice() {
        Invoice invoice = new Invoice();
        invoice.setId(1L);
        invoice.setAmount(BigDecimal.TEN);

        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));

        Invoice result = invoiceService.getInvoiceById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getAmount()).isEqualTo(BigDecimal.TEN);
        verify(invoiceRepository).findById(1L);
    }

    @Test
    void getInvoiceById_missing_throwsException() {
        when(invoiceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> invoiceService.getInvoiceById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invoice not found");
    }

    @Test
    void createInvoice_valid_setsPatientAppointmentDefaultsAndSaves() {
        // invoice primit din afară, cu id-uri la patient și appointment
        Patient patientRef = new Patient();
        patientRef.setId(10L);

        Appointment appointmentRef = new Appointment();
        appointmentRef.setId(20L);

        Invoice toCreate = new Invoice();
        toCreate.setPatient(patientRef);
        toCreate.setAppointment(appointmentRef);
        toCreate.setAmount(BigDecimal.valueOf(150));
        toCreate.setCurrency("RON");
        // issueDate lăsat null ca să vedem că se pune default
        // status lăsat null ca să fie UNPAID

        Patient persistedPatient = new Patient();
        persistedPatient.setId(10L);
        persistedPatient.setFirstName("Ana");

        Appointment persistedAppointment = new Appointment();
        persistedAppointment.setId(20L);

        when(patientRepository.findById(10L)).thenReturn(Optional.of(persistedPatient));
        when(appointmentRepository.findById(20L)).thenReturn(Optional.of(persistedAppointment));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(invocation -> {
            Invoice saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        Invoice created = invoiceService.createInvoice(toCreate);

        assertThat(created.getId()).isEqualTo(1L);
        assertThat(created.getPatient()).isSameAs(persistedPatient);
        assertThat(created.getAppointment()).isSameAs(persistedAppointment);
        assertThat(created.getStatus()).isEqualTo(InvoiceStatus.UNPAID);
        assertThat(created.getIssueDate()).isNotNull();

        verify(patientRepository).findById(10L);
        verify(appointmentRepository).findById(20L);
        verify(invoiceRepository).save(any(Invoice.class));
    }

    @Test
    void createInvoice_missingPatientId_throwsException() {
        Invoice invoice = new Invoice();
        invoice.setAmount(BigDecimal.TEN);
        // patient null sau fara id

        assertThatThrownBy(() -> invoiceService.createInvoice(invoice))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Patient id is required");
    }

    @Test
    void createInvoice_negativeOrNullAmount_throwsException() {
        Patient p = new Patient();
        p.setId(10L);

        // pentru ambele cazuri (null și negativ) avem nevoie ca pacientul să existe,
        // ca să ajungem la validarea "Amount must be positive"
        when(patientRepository.findById(10L)).thenReturn(Optional.of(p));

        // amount null
        Invoice invNull = new Invoice();
        invNull.setPatient(p);

        assertThatThrownBy(() -> invoiceService.createInvoice(invNull))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Amount must be positive");

        // amount negativ
        Invoice invNegative = new Invoice();
        invNegative.setPatient(p);
        invNegative.setAmount(BigDecimal.valueOf(-5));

        assertThatThrownBy(() -> invoiceService.createInvoice(invNegative))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Amount must be positive");
    }

    @Test
    void updateInvoice_updatesFieldsAndRelationsAndSaves() {
        // invoice existent
        Patient oldPatient = new Patient();
        oldPatient.setId(10L);

        Appointment oldAppointment = new Appointment();
        oldAppointment.setId(20L);

        Invoice existing = new Invoice();
        existing.setId(1L);
        existing.setPatient(oldPatient);
        existing.setAppointment(oldAppointment);
        existing.setAmount(BigDecimal.valueOf(100));
        existing.setCurrency("RON");
        existing.setIssueDate(LocalDate.of(2025, 1, 1));
        existing.setDueDate(LocalDate.of(2025, 1, 10));
        existing.setStatus(InvoiceStatus.UNPAID);
        existing.setDescription("Old desc");

        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(existing));

        // updated cu valori noi
        Patient newPatientRef = new Patient();
        newPatientRef.setId(30L);

        Appointment newAppointmentRef = new Appointment();
        newAppointmentRef.setId(40L);

        Invoice updated = new Invoice();
        updated.setAmount(BigDecimal.valueOf(200));
        updated.setCurrency("EUR");
        updated.setIssueDate(LocalDate.of(2025, 2, 1));
        updated.setDueDate(LocalDate.of(2025, 2, 15));
        updated.setStatus(InvoiceStatus.PAID);
        updated.setDescription("New desc");
        updated.setPatient(newPatientRef);
        updated.setAppointment(newAppointmentRef);

        Patient newPatient = new Patient();
        newPatient.setId(30L);

        Appointment newAppointment = new Appointment();
        newAppointment.setId(40L);

        when(patientRepository.findById(30L)).thenReturn(Optional.of(newPatient));
        when(appointmentRepository.findById(40L)).thenReturn(Optional.of(newAppointment));
        when(invoiceRepository.save(existing)).thenReturn(existing);

        Invoice result = invoiceService.updateInvoice(1L, updated);

        assertThat(result.getAmount()).isEqualTo(BigDecimal.valueOf(200));
        assertThat(result.getCurrency()).isEqualTo("EUR");
        assertThat(result.getIssueDate()).isEqualTo(LocalDate.of(2025, 2, 1));
        assertThat(result.getDueDate()).isEqualTo(LocalDate.of(2025, 2, 15));
        assertThat(result.getStatus()).isEqualTo(InvoiceStatus.PAID);
        assertThat(result.getDescription()).isEqualTo("New desc");
        assertThat(result.getPatient()).isSameAs(newPatient);
        assertThat(result.getAppointment()).isSameAs(newAppointment);

        verify(patientRepository).findById(30L);
        verify(appointmentRepository).findById(40L);
        verify(invoiceRepository).save(existing);
    }

    @Test
    void updateInvoice_negativeAmount_throwsException() {
        Invoice existing = new Invoice();
        existing.setId(1L);
        existing.setAmount(BigDecimal.valueOf(100));

        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(existing));

        Invoice updated = new Invoice();
        updated.setAmount(BigDecimal.valueOf(-10));

        assertThatThrownBy(() -> invoiceService.updateInvoice(1L, updated))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Amount must be positive");
    }

    @Test
    void deleteInvoice_existing_callsDeleteById() {
        when(invoiceRepository.existsById(1L)).thenReturn(true);

        invoiceService.deleteInvoice(1L);

        verify(invoiceRepository).deleteById(1L);
    }

    @Test
    void deleteInvoice_missing_throwsException() {
        when(invoiceRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> invoiceService.deleteInvoice(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invoice not found");
    }
}
