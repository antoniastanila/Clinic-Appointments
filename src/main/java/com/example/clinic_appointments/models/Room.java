package com.example.clinic_appointments.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Room name is required")
    @Size(max = 50)
    private String name; // ex: "Cabinet 101"

    @Size(max = 20)
    private String floor; // ex: "Etaj 1"

    @Size(max = 255)
    private String description; // optional: "Cardiologie", "Ecografie", etc.

    public Room() {
    }

    public Room(String name, String floor, String description) {
        this.name = name;
        this.floor = floor;
        this.description = description;
    }

    // getters & setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
