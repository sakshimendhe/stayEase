package com.example.stayEase.service;

import com.example.stayEase.exception.NotFoundException;
import com.example.stayEase.exception.UnauthorizedException;
import com.example.stayEase.model.Booking;
import com.example.stayEase.model.Hotel;
import com.example.stayEase.model.Role;
import com.example.stayEase.model.User;
import com.example.stayEase.repository.BookingRepository;
import com.example.stayEase.repository.HotelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private HotelRepository hotelRepository;

    @InjectMocks
    private BookingService bookingService;

    private User customer;
    private User manager;
    private Hotel hotel;
    private Booking booking;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        customer = User.builder().id(1L).role(Role.CUSTOMER).build();
        manager = User.builder().id(2L).role(Role.HOTEL_MANAGER).build();
        hotel = Hotel.builder().id(1L).availableRooms(10).build();
        booking = Booking.builder().id(1L).hotel(hotel).user(customer).isCancelled(false).build();
    }

    @Test
    void bookRoom_Successful() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        Booking result = bookingService.bookRoom(1L, customer);

        assertNotNull(result);
        assertEquals(customer, result.getUser());
        assertEquals(hotel, result.getHotel());
        verify(hotelRepository).save(hotel);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void bookRoom_HotelNotFound() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, 
                () -> bookingService.bookRoom(1L, customer));

        assertEquals("Hotel not found", exception.getMessage());
    }

    @Test
    void bookRoom_NoRoomsAvailable() {
        hotel.setAvailableRooms(0);
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));

        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> bookingService.bookRoom(1L, customer));

        assertEquals("No rooms available", exception.getMessage());
    }

    @Test
    void bookRoom_UnauthorizedUser() {
        customer.setRole(Role.HOTEL_MANAGER);

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, 
                () -> bookingService.bookRoom(1L, customer));

        assertEquals("Only customers can book rooms", exception.getMessage());
    }

    @Test
    void cancelBooking_Successful() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(hotelRepository.save(hotel)).thenReturn(hotel);

        bookingService.cancelBooking(1L, manager);

        assertTrue(booking.isCancelled());
        assertEquals(11, hotel.getAvailableRooms());
        verify(bookingRepository).save(booking);
        verify(hotelRepository).save(hotel);
    }

    @Test
    void cancelBooking_BookingNotFound() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, 
                () -> bookingService.cancelBooking(1L, manager));

        assertEquals("Booking not found", exception.getMessage());
    }

    @Test
    void cancelBooking_UnauthorizedUser() {
        manager.setRole(Role.CUSTOMER);

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, 
                () -> bookingService.cancelBooking(1L, manager));

        assertEquals("Only hotel managers can cancel bookings", exception.getMessage());
    }
}

