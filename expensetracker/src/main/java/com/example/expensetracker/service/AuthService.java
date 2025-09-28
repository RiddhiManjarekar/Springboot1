package com.example.expensetracker.service;

import com.example.expensetracker.dto.AuthResponse;
import com.example.expensetracker.dto.LoginRequest;
import com.example.expensetracker.dto.SignupRequest;

public interface AuthService{
         AuthResponse registerUser(SignupRequest request);
         AuthResponse authenticateUser(LoginRequest request);    
}