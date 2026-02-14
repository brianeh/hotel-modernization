package com.example.hotel.rooms.repository;

import com.example.hotel.rooms.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Integer> {
}
