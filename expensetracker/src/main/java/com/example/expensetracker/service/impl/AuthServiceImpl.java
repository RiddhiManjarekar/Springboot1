package com.example.expensetracker.service.impl;

import com.example.expensetracker.dto.AuthResponse;
import com.example.expensetracker.dto.LoginRequest;
import com.example.expensetracker.dto.SignupRequest;
import com.example.expensetracker.exception.ResourceNotFoundException;
import com.example.expensetracker.model.Role;
import com.example.expensetracker.model.User;
import com.example.expensetracker.repository.UserRepository;
import com.example.expensetracker.security.CustomUserDetails;
import com.example.expensetracker.service.AuthService;
import com.example.expensetracker.service.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService,
                           AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
public AuthResponse registerUser(SignupRequest request) {
    // Check if user already exists
    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
        throw new IllegalArgumentException("Email already taken");
    }

    // Create user with default role USER
    User user = new User();
    user.setFullName(request.getName());
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(Role.USER);  // Default role USER
    userRepository.save(user);

    // Do NOT generate token here (signup only registers)
    return AuthResponse.builder()
            .token("Signup successful, please login to get token")
            .role(user.getRole().name())
            .build();
}

@Override
public AuthResponse authenticateUser(LoginRequest request) {
    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
    );

    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    String jwt = jwtService.generateToken(userDetails);

    return AuthResponse.builder()
            .token(jwt)
            .role(userDetails.getUser().getRole().name())
            .build();
}
}
