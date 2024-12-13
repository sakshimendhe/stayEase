package com.example.stayEase.controller;
import org.springframework.web.bind.annotation.*;
import com.example.stayEase.model.Booking;
import com.example.stayEase.model.User;
import com.example.stayEase.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id, @AuthenticationPrincipal User manager) {
        bookingService.cancelBooking(id, manager);
        return ResponseEntity.noContent().build();
    }
}
