package com.example.stayEase.service;

import java.util.List;
import com.example.stayEase.repository.HotelRepository;
import com.example.stayEase.model.User;
import com.example.stayEase.exception.UnauthorizedException;
import com.example.stayEase.exception.NotFoundException;
import com.example.stayEase.model.Role;
import com.example.stayEase.model.Hotel;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@Service
public class HotelService {
     private final HotelRepository hotelRepository;

    public Hotel createHotel(Hotel hotel, User admin) {
        if (!admin.getRole().equals(Role.ADMIN)) {
            throw new UnauthorizedException("Only admins can create hotels");
        }
        return hotelRepository.save(hotel);
    }

    public void deleteHotel(Long id, User admin) {
        if (!admin.getRole().equals(Role.ADMIN)) {
            throw new UnauthorizedException("Only admins can delete hotels");
        }
        hotelRepository.deleteById(id);
    }

    public Hotel updateHotel(Long id, Hotel updatedHotel, User manager) {
        if (!manager.getRole().equals(Role.HOTEL_MANAGER)) {
            throw new UnauthorizedException("Only hotel managers can update hotel details");
        }
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Hotel not found"));
        hotel.setName(updatedHotel.getName());
        hotel.setLocation(updatedHotel.getLocation());
        hotel.setDescription(updatedHotel.getDescription());
        hotel.setAvailableRooms(updatedHotel.getAvailableRooms());
        return hotelRepository.save(hotel);
    }

    public List<Hotel> browseHotels() {
        return hotelRepository.findAll();
    }
}
