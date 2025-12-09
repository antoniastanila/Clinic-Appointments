package com.example.clinic_appointments.controller;

import com.example.clinic_appointments.model.Invoice;
import com.example.clinic_appointments.service.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    // GET /api/invoices -> toate facturile
    @GetMapping
    public List<Invoice> getAllInvoices() {
        return invoiceService.getAllInvoices();
    }

    // GET /api/invoices/{id} -> o factura dupa id
    @GetMapping("/{id}")
    public Invoice getInvoiceById(@PathVariable Long id) {
        return invoiceService.getInvoiceById(id);
    }

    // POST /api/invoices -> creeaza factura
    @PostMapping
    public ResponseEntity<Invoice> createInvoice(@Valid @RequestBody Invoice invoice) {
        Invoice created = invoiceService.createInvoice(invoice);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /api/invoices/{id} -> actualizeaza factura
    @PutMapping("/{id}")
    public Invoice updateInvoice(@PathVariable Long id,
                                 @Valid @RequestBody Invoice invoice) {
        return invoiceService.updateInvoice(id, invoice);
    }

    // DELETE /api/invoices/{id} -> sterge factura
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInvoice(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
    }
}
