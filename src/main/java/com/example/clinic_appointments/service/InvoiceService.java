package com.example.clinic_appointments.service;

import com.example.clinic_appointments.model.Appointment;
import com.example.clinic_appointments.model.Invoice;
import com.example.clinic_appointments.model.InvoiceStatus;
import com.example.clinic_appointments.model.Patient;
import com.example.clinic_appointments.repository.AppointmentRepository;
import com.example.clinic_appointments.repository.InvoiceRepository;
import com.example.clinic_appointments.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;

    public InvoiceService(InvoiceRepository invoiceRepository,
                          PatientRepository patientRepository,
                          AppointmentRepository appointmentRepository) {
        this.invoiceRepository = invoiceRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found with id " + id));
    }

    public Invoice createInvoice(Invoice invoice) {
        // validare pacient
        Long patientId = invoice.getPatient() != null ? invoice.getPatient().getId() : null;
        if (patientId == null) {
            throw new RuntimeException("Patient id is required for invoice");
        }

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found with id " + patientId));
        invoice.setPatient(patient);

        // appointment este optional, dar daca vine id, verificam ca exista
        if (invoice.getAppointment() != null && invoice.getAppointment().getId() != null) {
            Long appointmentId = invoice.getAppointment().getId();
            Appointment appointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> new RuntimeException("Appointment not found with id " + appointmentId));
            invoice.setAppointment(appointment);
        } else {
            invoice.setAppointment(null);
        }

        // suma nu trebuie sa fie negativa
        BigDecimal amount = invoice.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Amount must be positive");
        }

        // data emiterii default: azi, daca nu este setata
        if (invoice.getIssueDate() == null) {
            invoice.setIssueDate(LocalDate.now());
        }

        // status default: UNPAID
        if (invoice.getStatus() == null) {
            invoice.setStatus(InvoiceStatus.UNPAID);
        }

        return invoiceRepository.save(invoice);
    }

    public Invoice updateInvoice(Long id, Invoice updated) {
        Invoice existing = getInvoiceById(id);

        if (updated.getAmount() != null) {
            if (updated.getAmount().compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("Amount must be positive");
            }
            existing.setAmount(updated.getAmount());
        }

        if (updated.getCurrency() != null) {
            existing.setCurrency(updated.getCurrency());
        }

        if (updated.getIssueDate() != null) {
            existing.setIssueDate(updated.getIssueDate());
        }

        if (updated.getDueDate() != null) {
            existing.setDueDate(updated.getDueDate());
        }

        if (updated.getStatus() != null) {
            existing.setStatus(updated.getStatus());
        }

        if (updated.getDescription() != null) {
            existing.setDescription(updated.getDescription());
        }

        // optional: schimbare pacient / appointment
        if (updated.getPatient() != null && updated.getPatient().getId() != null) {
            Long patientId = updated.getPatient().getId();
            Patient patient = patientRepository.findById(patientId)
                    .orElseThrow(() -> new RuntimeException("Patient not found with id " + patientId));
            existing.setPatient(patient);
        }

        if (updated.getAppointment() != null && updated.getAppointment().getId() != null) {
            Long appointmentId = updated.getAppointment().getId();
            Appointment appointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> new RuntimeException("Appointment not found with id " + appointmentId));
            existing.setAppointment(appointment);
        }

        return invoiceRepository.save(existing);
    }

    public void deleteInvoice(Long id) {
        if (!invoiceRepository.existsById(id)) {
            throw new RuntimeException("Invoice not found with id " + id);
        }
        invoiceRepository.deleteById(id);
    }
}
