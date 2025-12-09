package com.example.clinic_appointments.controller;

import com.example.clinic_appointments.model.Room;
import com.example.clinic_appointments.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // GET /api/rooms -> lista tuturor camerelor
    @GetMapping
    public List<Room> getAllRooms() {
        return roomService.getAllRooms();
    }

    // GET /api/rooms/{id} -> o camera dupa id
    @GetMapping("/{id}")
    public Room getRoomById(@PathVariable Long id) {
        return roomService.getRoomById(id);
    }

    // POST /api/rooms -> creeaza camera
    @PostMapping
    public ResponseEntity<Room> createRoom(@Valid @RequestBody Room room) {
        Room created = roomService.createRoom(room);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /api/rooms/{id} -> actualizeaza camera
    @PutMapping("/{id}")
    public Room updateRoom(@PathVariable Long id, @Valid @RequestBody Room room) {
        return roomService.updateRoom(id, room);
    }

    // DELETE /api/rooms/{id} -> sterge camera
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
    }
}
