package com.example.stayEase.security;

import com.example.stayEase.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**","/auth/register","/auth/login").permitAll() // Public endpoints
                 .requestMatchers(HttpMethod.GET, "/api/hotels").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/hotels").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/api/hotels/**").hasRole("HOTEL_MANAGER")
            .requestMatchers(HttpMethod.DELETE, "/api/hotels/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.POST, "/api/bookings/**").hasRole("CUSTOMER")
            .requestMatchers(HttpMethod.DELETE, "/api/bookings/**").hasRole("HOTEL_MANAGER")
            .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // JWT filter

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
