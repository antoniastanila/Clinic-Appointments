package com.example.clinic_appointments.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "doctor_availabilities")
public class DoctorAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many availabilities -> one doctor
    @ManyToOne(optional = false)
    @JoinColumn(name = "doctor_id")
    @NotNull(message = "Doctor is required")
    private Doctor doctor;

    @NotNull(message = "Day of week is required")
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;   // LUNI, MARTI etc. (in engleza: MONDAY, TUESDAY...)

    @NotNull(message = "Start time is required")
    private LocalTime startTime;   // ex: 09:00

    @NotNull(message = "End time is required")
    private LocalTime endTime;     // ex: 13:00

    public DoctorAvailability() {
    }

    public DoctorAvailability(Doctor doctor,
                              DayOfWeek dayOfWeek,
                              LocalTime startTime,
                              LocalTime endTime) {
        this.doctor = doctor;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // getters & setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}
