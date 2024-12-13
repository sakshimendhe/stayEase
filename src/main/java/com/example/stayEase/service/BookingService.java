package com.example.stayEase.service;
import com.example.stayEase.model.User;
import com.example.stayEase.model.Role;
import com.example.stayEase.repository.BookingRepository;
import com.example.stayEase.repository.HotelRepository;
import com.example.stayEase.model.Booking;
import com.example.stayEase.model.Hotel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.stayEase.exception.UnauthorizedException;
import com.example.stayEase.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class BookingService {
     private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;

    public Booking bookRoom(Long hotelId, User customer) {
        if (!customer.getRole().equals(Role.CUSTOMER)) {
            throw new UnauthorizedException("Only customers can book rooms");
        }

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new NotFoundException("Hotel not found"));

        if (hotel.getAvailableRooms() <= 0) {
            throw new RuntimeException("No rooms available");
        }

        hotel.setAvailableRooms(hotel.getAvailableRooms() - 1);
        hotelRepository.save(hotel);

        Booking booking = Booking.builder()
                .hotel(hotel)
                .user(customer)
                .isCancelled(false)
                .build();
        return bookingRepository.save(booking);
    }

    public void cancelBooking(Long bookingId, User manager) {
        if (!manager.getRole().equals(Role.HOTEL_MANAGER)) {
            throw new UnauthorizedException("Only hotel managers can cancel bookings");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        booking.setCancelled(true);
        booking.getHotel().setAvailableRooms(booking.getHotel().getAvailableRooms() + 1);
        bookingRepository.save(booking);
        hotelRepository.save(booking.getHotel());
    }
}
