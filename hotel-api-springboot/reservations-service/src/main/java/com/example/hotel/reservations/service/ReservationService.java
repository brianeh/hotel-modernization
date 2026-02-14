package com.example.hotel.reservations.service;

import com.example.hotel.reservations.model.Reservation;
import com.example.hotel.reservations.model.Room;
import com.example.hotel.reservations.repository.ReservationRepository;
import com.example.hotel.reservations.repository.RoomRepository;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    public ReservationService(ReservationRepository reservationRepository, RoomRepository roomRepository) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> getReservation(Integer id) {
        return reservationRepository.findById(id);
    }

    public ResponseEntity<Reservation> createReservation(Reservation reservation) {
        Reservation saved = reservationRepository.save(reservation);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(saved.getId())
            .toUri();
        return ResponseEntity.created(location).body(saved);
    }

    public void updateReservation(Integer id, Reservation reservation) {
        reservation.setId(id);
        reservationRepository.save(reservation);
    }

    public void deleteReservation(Integer id) {
        reservationRepository.findById(id).ifPresent(reservationRepository::delete);
    }

    public List<Room> searchAvailability(LocalDate checkIn, LocalDate checkOut) {
        List<Room> availableRooms = new ArrayList<>(roomRepository.findAll());
        List<Reservation> reservations = reservationRepository.findAll();

        for (Reservation reservation : reservations) {
            if (hasDateConflict(checkIn, checkOut, reservation.getCheckInDate(), reservation.getCheckOutDate())) {
                availableRooms.removeIf(room -> room.getId().equals(reservation.getIdRoom()));
            }
        }

        return availableRooms;
    }

    private boolean hasDateConflict(LocalDate checkIn, LocalDate checkOut,
                                    LocalDate reservationCheckIn, LocalDate reservationCheckOut) {
        if (checkIn == null || checkOut == null || reservationCheckIn == null || reservationCheckOut == null) {
            return false;
        }
        return !checkOut.isBefore(reservationCheckIn) && !checkIn.isAfter(reservationCheckOut);
    }
}
