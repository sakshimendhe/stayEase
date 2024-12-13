package com.example.stayEase.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.stayEase.model.User;
import com.example.stayEase.service.HotelService;
import com.example.stayEase.service.BookingService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

import lombok.RequiredArgsConstructor;

import com.example.stayEase.model.Booking;
import com.example.stayEase.model.Hotel;
@RestController
@RequestMapping("/api/hotels")
@RequiredArgsConstructor
public class HotelController {
     private final HotelService hotelService;
     private final BookingService bookingService;
      @PostMapping("/{hotelId}/book")
    public ResponseEntity<Booking> bookRoom(@PathVariable Long hotelId, @AuthenticationPrincipal User customer) {
        return ResponseEntity.ok(bookingService.bookRoom(hotelId, customer));
    }

    @PostMapping
    public ResponseEntity<Hotel> createHotel(@RequestBody Hotel hotel, @AuthenticationPrincipal User admin) {
        return ResponseEntity.ok(hotelService.createHotel(hotel, admin));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Hotel> updateHotel(@PathVariable Long id, @RequestBody Hotel hotel, @AuthenticationPrincipal User manager) {
        return ResponseEntity.ok(hotelService.updateHotel(id, hotel, manager));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHotel(@PathVariable Long id, @AuthenticationPrincipal User admin) {
        hotelService.deleteHotel(id, admin);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Hotel>> browseHotels() {
        return ResponseEntity.ok(hotelService.browseHotels());
    }
}
