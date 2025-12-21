package com.example.clinic_appointments.controller;

import com.example.clinic_appointments.model.Appointment;
import com.example.clinic_appointments.model.Invoice;
import com.example.clinic_appointments.model.InvoiceStatus;
import com.example.clinic_appointments.model.Patient;
import com.example.clinic_appointments.service.InvoiceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InvoiceController.class)
class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InvoiceService invoiceService;

    @Test
    void getAllInvoices_returnsList() throws Exception {
        Invoice i1 = new Invoice();
        i1.setId(1L);
        i1.setAmount(BigDecimal.valueOf(100));
        i1.setCurrency("RON");

        given(invoiceService.getAllInvoices()).willReturn(List.of(i1));

        mockMvc.perform(get("/api/invoices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].amount").value(100));
    }

    @Test
    void getInvoiceById_returnsInvoice() throws Exception {
        Invoice invoice = new Invoice();
        invoice.setId(1L);
        invoice.setAmount(BigDecimal.valueOf(150));
        invoice.setCurrency("EUR");
        invoice.setStatus(InvoiceStatus.UNPAID);

        given(invoiceService.getInvoiceById(1L)).willReturn(invoice);

        mockMvc.perform(get("/api/invoices/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.amount").value(150))
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.status").value("UNPAID"));
    }

    @Test
    void createInvoice_returnsCreated() throws Exception {
        Patient p = new Patient();
        p.setId(10L);

        Invoice created = new Invoice();
        created.setId(1L);
        created.setPatient(p);
        created.setAmount(BigDecimal.valueOf(200));
        created.setCurrency("RON");
        created.setIssueDate(LocalDate.of(2025, 1, 1));
        created.setStatus(InvoiceStatus.UNPAID);

        given(invoiceService.createInvoice(any(Invoice.class))).willReturn(created);

        String requestBody = """
        {
          "patient": {
            "id": 10,
            "firstName": "Ana",
            "lastName": "Popescu",
            "email": "ana@example.com",
            "phone": "0712345678",
            "dateOfBirth": "1990-01-01"
          },
          "amount": 200,
          "currency": "RON",
          "description": "Consultation fee",
          "issueDate": "2025-01-01",
          "dueDate": "2025-01-10",
          "status": "UNPAID"
        }
        """;

        mockMvc.perform(post("/api/invoices")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.amount").value(200))
                .andExpect(jsonPath("$.currency").value("RON"))
                .andExpect(jsonPath("$.patient.id").value(10));
    }

    @Test
    void updateInvoice_returnsUpdated() throws Exception {
        Patient p = new Patient();
        p.setId(10L);

        Appointment a = new Appointment();
        a.setId(20L);

        Invoice updated = new Invoice();
        updated.setId(1L);
        updated.setPatient(p);
        updated.setAppointment(a);
        updated.setAmount(BigDecimal.valueOf(300));
        updated.setCurrency("EUR");
        updated.setStatus(InvoiceStatus.PAID);
        updated.setDescription("Updated desc");

        given(invoiceService.updateInvoice(any(Long.class), any(Invoice.class)))
                .willReturn(updated);

        String requestBody = """
        {
          "patient": {
            "id": 10,
            "firstName": "Ana",
            "lastName": "Popescu",
            "email": "ana@example.com",
            "phone": "0712345678",
            "dateOfBirth": "1990-01-01"
          },
          "appointment": {
            "id": 20
          },
          "amount": 300,
          "currency": "EUR",
          "issueDate": "2025-02-01",
          "dueDate": "2025-02-15",
          "status": "PAID",
          "description": "Updated desc"
        }
        """;


        mockMvc.perform(put("/api/invoices/1")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.amount").value(300))
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.status").value("PAID"))
                .andExpect(jsonPath("$.patient.id").value(10))
                .andExpect(jsonPath("$.appointment.id").value(20));
    }

    @Test
    void deleteInvoice_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/invoices/1"))
                .andExpect(status().isNoContent());

        verify(invoiceService).deleteInvoice(1L);
    }
}
