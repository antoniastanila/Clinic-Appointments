package com.example.clinic_appointments.controller;

import com.example.clinic_appointments.model.Patient;
import com.example.clinic_appointments.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PatientController.class) // noul pachet pentru Spring Boot 4
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // În Spring Boot 4 nu mai folosim @MockBean, ci @MockitoBean
    @MockitoBean
    private PatientService patientService;

    @Test
    void getAllPatients_returnsList() throws Exception {
        Patient p1 = new Patient();
        p1.setId(1L);
        p1.setFirstName("John");
        p1.setLastName("Doe");
        p1.setEmail("john@example.com");
        p1.setPhone("123456");
        p1.setDateOfBirth(LocalDate.of(1990, 1, 1));

        given(patientService.getAllPatients()).willReturn(List.of(p1));

        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].firstName").value("John"));
    }

    @Test
    void createPatient_returnsCreated() throws Exception {
        Patient created = new Patient();
        created.setId(1L);
        created.setFirstName("John");
        created.setLastName("Doe");
        created.setEmail("john@example.com");
        created.setPhone("123456");
        created.setDateOfBirth(LocalDate.of(1990, 1, 1));

        given(patientService.createPatient(any(Patient.class))).willReturn(created);

        // Scriem JSON-ul manual, ca să nu mai depindem de ObjectMapper
        String requestBody = """
                {
                  "firstName": "John",
                  "lastName": "Doe",
                  "email": "john@example.com",
                  "phone": "123456",
                  "dateOfBirth": "1990-01-01"
                }
                """;

        mockMvc.perform(post("/api/patients")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("John"));
    }
}
