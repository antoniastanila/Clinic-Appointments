package com.example.clinic_appointments.controller;

import com.example.clinic_appointments.model.Doctor;
import com.example.clinic_appointments.model.DoctorAvailability;
import com.example.clinic_appointments.service.DoctorAvailabilityService;
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

@WebMvcTest(DoctorAvailabilityController.class)
class DoctorAvailabilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DoctorAvailabilityService availabilityService;

    @Test
    void getAllAvailabilities_returnsList() throws Exception {
        Doctor doctor = new Doctor();
        doctor.setId(10L);

        DoctorAvailability a1 = new DoctorAvailability();
        a1.setId(1L);
        a1.setDoctor(doctor);

        given(availabilityService.getAllAvailabilities()).willReturn(List.of(a1));

        mockMvc.perform(get("/api/doctor-availabilities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].doctor.id").value(10L));
    }

    @Test
    void getAvailabilityById_returnsAvailability() throws Exception {
        Doctor doctor = new Doctor();
        doctor.setId(10L);

        DoctorAvailability availability = new DoctorAvailability();
        availability.setId(1L);
        availability.setDoctor(doctor);

        given(availabilityService.getAvailabilityById(1L)).willReturn(availability);

        mockMvc.perform(get("/api/doctor-availabilities/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.doctor.id").value(10L));
    }

    @Test
    void createAvailability_returnsCreated() throws Exception {
        Doctor doctor = new Doctor();
        doctor.setId(10L);

        DoctorAvailability created = new DoctorAvailability();
        created.setId(1L);
        created.setDoctor(doctor);

        given(availabilityService.createAvailability(any(DoctorAvailability.class)))
                .willReturn(created);

        String requestBody = """
                {
                  "doctor": { "id": 10 },
                  "dayOfWeek": "MONDAY",
                  "startTime": "09:00:00",
                  "endTime": "12:00:00"
                }
                """;

        mockMvc.perform(post("/api/doctor-availabilities")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.doctor.id").value(10L));
    }

    @Test
    void updateAvailability_returnsUpdated() throws Exception {
        Doctor doctor = new Doctor();
        doctor.setId(20L);

        DoctorAvailability updated = new DoctorAvailability();
        updated.setId(1L);
        updated.setDoctor(doctor);

        given(availabilityService.updateAvailability(any(Long.class), any(DoctorAvailability.class)))
                .willReturn(updated);

        String requestBody = """
                {
                  "doctor": { "id": 20 },
                  "dayOfWeek": "TUESDAY",
                  "startTime": "10:00:00",
                  "endTime": "13:00:00"
                }
                """;

        mockMvc.perform(put("/api/doctor-availabilities/1")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.doctor.id").value(20L));
    }

    @Test
    void deleteAvailability_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/doctor-availabilities/1"))
                .andExpect(status().isNoContent());

        verify(availabilityService).deleteAvailability(1L);
    }
}
