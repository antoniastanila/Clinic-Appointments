package com.example.clinic_appointments.controller;

import com.example.clinic_appointments.model.Doctor;
import com.example.clinic_appointments.service.DoctorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DoctorController.class)
class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DoctorService doctorService;

    @Test
    void getAllDoctors_returnsList() throws Exception {
        Doctor d1 = new Doctor();
        d1.setId(1L);
        d1.setFirstName("Ana");
        d1.setLastName("Popescu");

        given(doctorService.getAllDoctors()).willReturn(List.of(d1));

        mockMvc.perform(get("/api/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].firstName").value("Ana"));
    }

    @Test
    void getDoctorById_returnsDoctor() throws Exception {
        Doctor d = new Doctor();
        d.setId(1L);
        d.setFirstName("Ana");
        d.setLastName("Popescu");

        given(doctorService.getDoctorById(1L)).willReturn(d);

        mockMvc.perform(get("/api/doctors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("Ana"));
    }

    @Test
    void createDoctor_returnsCreated() throws Exception {
        Doctor created = new Doctor();
        created.setId(1L);
        created.setFirstName("Ana");
        created.setLastName("Popescu");
        created.setEmail("ana@example.com");
        created.setPhone("0712345678");
        created.setSpecialization("Cardiology");

        given(doctorService.createDoctor(any(Doctor.class))).willReturn(created);

        String requestBody = """
                {
                  "firstName": "Ana",
                  "lastName": "Popescu",
                  "email": "ana@example.com",
                  "phone": "0712345678",
                  "specialization": "Cardiology"
                }
                """;

        mockMvc.perform(post("/api/doctors")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("Ana"))
                .andExpect(jsonPath("$.specialization").value("Cardiology"));
    }

    @Test
    void updateDoctor_returnsUpdated() throws Exception {
        Doctor updated = new Doctor();
        updated.setId(1L);
        updated.setFirstName("New");
        updated.setLastName("Name");
        updated.setEmail("new@example.com");
        updated.setPhone("0711111111");
        updated.setSpecialization("NewSpec");

        given(doctorService.updateDoctor(any(Long.class), any(Doctor.class)))
                .willReturn(updated);

        String requestBody = """
                {
                  "firstName": "New",
                  "lastName": "Name",
                  "email": "new@example.com",
                  "phone": "0711111111",
                  "specialization": "NewSpec"
                }
                """;

        mockMvc.perform(put("/api/doctors/1")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("New"))
                .andExpect(jsonPath("$.specialization").value("NewSpec"));
    }

    @Test
    void deleteDoctor_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/doctors/1"))
                .andExpect(status().isNoContent());

        verify(doctorService).deleteDoctor(1L);
    }
}
