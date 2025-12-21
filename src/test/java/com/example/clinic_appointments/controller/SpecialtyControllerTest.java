package com.example.clinic_appointments.controller;

import com.example.clinic_appointments.model.Specialty;
import com.example.clinic_appointments.service.SpecialtyService;
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

@WebMvcTest(SpecialtyController.class)
class SpecialtyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SpecialtyService specialtyService;

    @Test
    void getAllSpecialties_returnsList() throws Exception {
        Specialty s1 = new Specialty();
        s1.setId(1L);
        s1.setName("Cardiology");
        s1.setDescription("Heart related");

        given(specialtyService.getAllSpecialties()).willReturn(List.of(s1));

        mockMvc.perform(get("/api/specialties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Cardiology"))
                .andExpect(jsonPath("$[0].description").value("Heart related"));
    }

    @Test
    void getSpecialtyById_returnsSpecialty() throws Exception {
        Specialty s = new Specialty();
        s.setId(1L);
        s.setName("Cardiology");
        s.setDescription("Heart related");

        given(specialtyService.getSpecialtyById(1L)).willReturn(s);

        mockMvc.perform(get("/api/specialties/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Cardiology"))
                .andExpect(jsonPath("$.description").value("Heart related"));
    }

    @Test
    void createSpecialty_returnsCreated() throws Exception {
        Specialty created = new Specialty();
        created.setId(1L);
        created.setName("Cardiology");
        created.setDescription("Heart related");

        given(specialtyService.createSpecialty(any(Specialty.class))).willReturn(created);

        String requestBody = """
                {
                  "name": "Cardiology",
                  "description": "Heart related"
                }
                """;

        mockMvc.perform(post("/api/specialties")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Cardiology"))
                .andExpect(jsonPath("$.description").value("Heart related"));
    }

    @Test
    void updateSpecialty_returnsUpdated() throws Exception {
        Specialty updated = new Specialty();
        updated.setId(1L);
        updated.setName("Updated name");
        updated.setDescription("Updated desc");

        given(specialtyService.updateSpecialty(any(Long.class), any(Specialty.class)))
                .willReturn(updated);

        String requestBody = """
                {
                  "name": "Updated name",
                  "description": "Updated desc"
                }
                """;

        mockMvc.perform(put("/api/specialties/1")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated name"))
                .andExpect(jsonPath("$.description").value("Updated desc"));
    }

    @Test
    void deleteSpecialty_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/specialties/1"))
                .andExpect(status().isNoContent());

        verify(specialtyService).deleteSpecialty(1L);
    }
}
