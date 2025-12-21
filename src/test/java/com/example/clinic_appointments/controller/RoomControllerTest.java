package com.example.clinic_appointments.controller;

import com.example.clinic_appointments.model.Room;
import com.example.clinic_appointments.service.RoomService;
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

@WebMvcTest(RoomController.class)
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoomService roomService;

    @Test
    void getAllRooms_returnsList() throws Exception {
        Room r1 = new Room();
        r1.setId(1L);
        r1.setName("Room A");
        r1.setFloor("1");
        r1.setDescription("General");

        given(roomService.getAllRooms()).willReturn(List.of(r1));

        mockMvc.perform(get("/api/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Room A"))
                .andExpect(jsonPath("$[0].floor").value("1"))
                .andExpect(jsonPath("$[0].description").value("General"));
    }

    @Test
    void getRoomById_returnsRoom() throws Exception {
        Room r = new Room();
        r.setId(1L);
        r.setName("Room A");
        r.setFloor("1");
        r.setDescription("General");

        given(roomService.getRoomById(1L)).willReturn(r);

        mockMvc.perform(get("/api/rooms/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Room A"))
                .andExpect(jsonPath("$.floor").value("1"))
                .andExpect(jsonPath("$.description").value("General"));
    }

    @Test
    void createRoom_returnsCreated() throws Exception {
        Room created = new Room();
        created.setId(1L);
        created.setName("Room A");
        created.setFloor("1");
        created.setDescription("General");

        given(roomService.createRoom(any(Room.class))).willReturn(created);

        String requestBody = """
                {
                  "name": "Room A",
                  "floor": "1",
                  "description": "General"
                }
                """;

        mockMvc.perform(post("/api/rooms")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Room A"))
                .andExpect(jsonPath("$.floor").value("1"))
                .andExpect(jsonPath("$.description").value("General"));
    }

    @Test
    void updateRoom_returnsUpdated() throws Exception {
        Room updated = new Room();
        updated.setId(1L);
        updated.setName("Updated Room");
        updated.setFloor("2");
        updated.setDescription("Updated desc");

        given(roomService.updateRoom(any(Long.class), any(Room.class)))
                .willReturn(updated);

        String requestBody = """
                {
                  "name": "Updated Room",
                  "floor": "2",
                  "description": "Updated desc"
                }
                """;

        mockMvc.perform(put("/api/rooms/1")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Room"))
                .andExpect(jsonPath("$.floor").value("2"))
                .andExpect(jsonPath("$.description").value("Updated desc"));
    }

    @Test
    void deleteRoom_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/rooms/1"))
                .andExpect(status().isNoContent());

        verify(roomService).deleteRoom(1L);
    }
}
