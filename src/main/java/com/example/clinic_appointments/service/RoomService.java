package com.example.clinic_appointments.service;

import com.example.clinic_appointments.model.Room;
import com.example.clinic_appointments.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with id " + id));
    }

    public Room createRoom(Room room) {
        return roomRepository.save(room);
    }

    public Room updateRoom(Long id, Room updated) {
        Room existing = getRoomById(id);

        existing.setName(updated.getName());
        existing.setFloor(updated.getFloor());
        existing.setDescription(updated.getDescription());

        return roomRepository.save(existing);
    }

    public void deleteRoom(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new RuntimeException("Room not found with id " + id);
        }
        roomRepository.deleteById(id);
    }
}
