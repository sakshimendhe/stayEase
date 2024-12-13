package com.example.stayEase.service;

import com.example.stayEase.exception.NotFoundException;
import com.example.stayEase.exception.UnauthorizedException;
import com.example.stayEase.model.Hotel;
import com.example.stayEase.model.Role;
import com.example.stayEase.model.User;
import com.example.stayEase.repository.HotelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HotelServiceTest {

    @Mock
    private HotelRepository hotelRepository;

    @InjectMocks
    private HotelService hotelService;

    private User admin;
    private User manager;
    private Hotel hotel;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        admin = User.builder().id(1L).role(Role.ADMIN).build();
        manager = User.builder().id(2L).role(Role.HOTEL_MANAGER).build();

        hotel = Hotel.builder()
                .id(1L)
                .name("Hotel California")
                .location("California")
                .description("A lovely place")
                .availableRooms(10)
                .build();
    }

    @Test
    void createHotel_Successful() {
        when(hotelRepository.save(hotel)).thenReturn(hotel);

        Hotel result = hotelService.createHotel(hotel, admin);

        assertNotNull(result);
        assertEquals("Hotel California", result.getName());
        verify(hotelRepository).save(hotel);
    }

    @Test
    void createHotel_UnauthorizedUser() {
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, 
                () -> hotelService.createHotel(hotel, manager));

        assertEquals("Only admins can create hotels", exception.getMessage());
        verify(hotelRepository, never()).save(any(Hotel.class));
    }

    @Test
    void deleteHotel_Successful() {
        doNothing().when(hotelRepository).deleteById(1L);

        hotelService.deleteHotel(1L, admin);

        verify(hotelRepository).deleteById(1L);
    }

    @Test
    void deleteHotel_UnauthorizedUser() {
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, 
                () -> hotelService.deleteHotel(1L, manager));

        assertEquals("Only admins can delete hotels", exception.getMessage());
        verify(hotelRepository, never()).deleteById(anyLong());
    }

    @Test
    void updateHotel_Successful() {
        Hotel updatedHotel = Hotel.builder()
                .name("Updated Hotel")
                .location("New Location")
                .description("Updated Description")
                .availableRooms(20)
                .build();

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(hotelRepository.save(hotel)).thenReturn(updatedHotel);

        Hotel result = hotelService.updateHotel(1L, updatedHotel, manager);

        assertNotNull(result);
        assertEquals("Updated Hotel", result.getName());
        assertEquals("New Location", result.getLocation());
        verify(hotelRepository).save(hotel);
    }

    @Test
    void updateHotel_HotelNotFound() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, 
                () -> hotelService.updateHotel(1L, hotel, manager));

        assertEquals("Hotel not found", exception.getMessage());
        verify(hotelRepository, never()).save(any(Hotel.class));
    }

    @Test
    void updateHotel_UnauthorizedUser() {
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, 
                () -> hotelService.updateHotel(1L, hotel, admin));

        assertEquals("Only hotel managers can update hotel details", exception.getMessage());
        verify(hotelRepository, never()).save(any(Hotel.class));
    }

    @Test
    void browseHotels_Successful() {
        List<Hotel> hotels = Arrays.asList(
                Hotel.builder().name("Hotel A").build(),
                Hotel.builder().name("Hotel B").build()
        );

        when(hotelRepository.findAll()).thenReturn(hotels);

        List<Hotel> result = hotelService.browseHotels();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(hotelRepository).findAll();
    }
}
