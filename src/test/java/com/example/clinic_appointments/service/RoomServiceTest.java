package com.example.clinic_appointments.service;

import com.example.clinic_appointments.model.Room;
import com.example.clinic_appointments.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomService roomService;

    @Test
    void getAllRooms_returnsListFromRepository() {
        Room r1 = new Room();
        r1.setId(1L);
        r1.setName("Room A");
        r1.setFloor("1");

        Room r2 = new Room();
        r2.setId(2L);
        r2.setName("Room B");
        r2.setFloor("2");

        when(roomRepository.findAll()).thenReturn(List.of(r1, r2));

        List<Room> result = roomService.getAllRooms();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Room A");
        assertThat(result.get(0).getFloor()).isEqualTo("1");
        verify(roomRepository, times(1)).findAll();
    }

    @Test
    void getRoomById_existing_returnsRoom() {
        Room r = new Room();
        r.setId(1L);
        r.setName("Room A");
        r.setFloor("1");

        when(roomRepository.findById(1L)).thenReturn(Optional.of(r));

        Room result = roomService.getRoomById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Room A");
        assertThat(result.getFloor()).isEqualTo("1");
        verify(roomRepository).findById(1L);
    }

    @Test
    void getRoomById_missing_throwsException() {
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.getRoomById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Room not found");
    }

    @Test
    void createRoom_savesToRepository() {
        Room r = new Room();
        r.setName("Room A");
        r.setFloor("1");
        r.setDescription("General consultation");

        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> {
            Room saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        Room created = roomService.createRoom(r);

        assertThat(created.getId()).isEqualTo(1L);
        verify(roomRepository).save(any(Room.class));
    }

    @Test
    void updateRoom_updatesFieldsAndSaves() {
        Room existing = new Room();
        existing.setId(1L);
        existing.setName("Old name");
        existing.setFloor("1");
        existing.setDescription("Old desc");

        when(roomRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(roomRepository.save(existing)).thenReturn(existing);

        Room updated = new Room();
        updated.setName("New name");
        updated.setFloor("2");
        updated.setDescription("New desc");

        Room result = roomService.updateRoom(1L, updated);

        assertThat(result.getName()).isEqualTo("New name");
        assertThat(result.getFloor()).isEqualTo("2");
        assertThat(result.getDescription()).isEqualTo("New desc");
        verify(roomRepository).save(existing);
    }

    @Test
    void deleteRoom_existing_callsDeleteById() {
        when(roomRepository.existsById(1L)).thenReturn(true);

        roomService.deleteRoom(1L);

        verify(roomRepository).deleteById(1L);
    }

    @Test
    void deleteRoom_missing_throwsException() {
        when(roomRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> roomService.deleteRoom(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Room not found");
    }
}
