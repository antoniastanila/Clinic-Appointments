package com.example.clinic_appointments.repository;

import com.example.clinic_appointments.model.Invoice;
import com.example.clinic_appointments.model.InvoiceStatus;
import com.example.clinic_appointments.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByPatient(Patient patient);

    List<Invoice> findByPatientAndStatus(Patient patient, InvoiceStatus status);
}
