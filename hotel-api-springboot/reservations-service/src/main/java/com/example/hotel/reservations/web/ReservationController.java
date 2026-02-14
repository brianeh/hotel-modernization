package com.example.hotel.reservations.web;

import com.example.hotel.reservations.model.Reservation;
import com.example.hotel.reservations.model.Room;
import com.example.hotel.reservations.service.ReservationService;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public List<Reservation> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservation(@PathVariable("id") Integer id) {
        return reservationService.getReservation(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchAvailability(@RequestParam("checkIn") String checkInStr,
                                                @RequestParam("checkOut") String checkOutStr) {
        if (checkInStr == null || checkOutStr == null) {
            return ResponseEntity.badRequest().body("Both checkIn and checkOut parameters are required");
        }

        try {
            LocalDate checkIn = LocalDate.parse(checkInStr);
            LocalDate checkOut = LocalDate.parse(checkOutStr);
            List<Room> availableRooms = reservationService.searchAvailability(checkIn, checkOut);
            return ResponseEntity.ok(availableRooms);
        } catch (DateTimeParseException ex) {
            return ResponseEntity.badRequest().body("Invalid data format. Use yyyy-MM-dd");
        }
    }

    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservation) {
        return reservationService.createReservation(reservation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateReservation(@PathVariable("id") Integer id,
                                                  @RequestBody Reservation reservation) {
        reservationService.updateReservation(id, reservation);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable("id") Integer id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}
