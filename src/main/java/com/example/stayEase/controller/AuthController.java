package com.example.stayEase.controller;

import com.example.stayEase.dto.AuthRequest;
import com.example.stayEase.dto.AuthResponse;
import com.example.stayEase.model.User;
import com.example.stayEase.service.UserService;
import com.example.stayEase.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
     private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.saveUser(user);

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().toString());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        User user = userService.findByEmail(request.getEmail());
    
        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            // Generate token with email and role
            String token = jwtUtil.generateToken(user.getEmail(), user.getRole().toString());
            return ResponseEntity.ok(new AuthResponse(token));
        } else {
            return ResponseEntity.status(401).build();
        }
    }
    
}
