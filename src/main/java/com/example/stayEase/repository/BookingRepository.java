package com.example.stayEase.repository;

import com.example.stayEase.model.Booking;
import com.example.stayEase.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Find a booking by its ID
    Optional<Booking> findById(Long id);

    // Find all bookings for a specific hotel
    List<Booking> findByHotel(Hotel hotel);

    // Find all active bookings (not cancelled) for a specific hotel
    List<Booking> findByHotelAndIsCancelledFalse(Hotel hotel);

    // Custom query example to find bookings for a hotel and user
    @Query("SELECT b FROM Booking b WHERE b.hotel.id = :hotelId AND b.user.id = :userId AND b.isCancelled = false")
    Optional<Booking> findActiveBookingForHotelAndUser(Long hotelId, Long userId);
}
