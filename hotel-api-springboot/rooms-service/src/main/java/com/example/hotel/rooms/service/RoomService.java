package com.example.hotel.rooms.service;

import com.example.hotel.rooms.model.Room;
import com.example.hotel.rooms.repository.RoomRepository;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Optional<Room> getRoom(Integer id) {
        return roomRepository.findById(id);
    }

    public ResponseEntity<Room> createRoom(Room room) {
        Room saved = roomRepository.save(room);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(saved.getId())
            .toUri();
        return ResponseEntity.created(location).body(saved);
    }

    public void updateRoom(Integer id, Room room) {
        room.setId(id);
        roomRepository.save(room);
    }

    public void deleteRoom(Integer id) {
        roomRepository.findById(id).ifPresent(roomRepository::delete);
    }
}
