package com.example.banking.service;

import com.example.banking.dto.RegisterRequest;
import com.example.banking.dto.LoginRequest;
import com.example.banking.dto.AuthResponse;
import com.example.banking.model.User;
import com.example.banking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.banking.security.JwtUtil;
import com.example.banking.model.Account;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager; // <-- add this

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return new AuthResponse("Email already exists", null);
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .build();

        Account account = Account.builder()
                .accountNumber(generateAccountNumber())
                .balance(BigDecimal.ZERO)
                .user(user)
                .build();

        user.setAccount(account);

        userRepository.save(user);

        return new AuthResponse("User registered successfully", null);
    }

    private String generateAccountNumber() {
        return String.valueOf(System.currentTimeMillis()).substring(3, 13);
    }

    public AuthResponse login(LoginRequest request) {
    System.out.println("Attempting login for: " + request.getEmail());
    try {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        System.out.println("Authentication successful");
    } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("Invalid email or password");
    }
    
    User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));
    
    String token = jwtUtil.generateToken(user.getEmail());
    return new AuthResponse("Login successful", token);
}


    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
