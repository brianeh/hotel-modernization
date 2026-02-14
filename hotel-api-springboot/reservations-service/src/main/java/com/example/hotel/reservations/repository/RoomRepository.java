package com.example.hotel.reservations.repository;

import com.example.hotel.reservations.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Integer> {
}
