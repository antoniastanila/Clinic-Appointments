package com.example.clinic_appointments.repository;

import com.example.clinic_appointments.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    // putem adauga metode custom mai tarziu, de ex:
    // List<Room> findByFloor(String floor);
}
