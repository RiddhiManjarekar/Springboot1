package com.example.expensetracker.mapper;

import com.example.expensetracker.dto.UserDTO;
import com.example.expensetracker.model.Role;
import com.example.expensetracker.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDTO toDTO(User user) {
        return new UserDTO(user.getId(), user.getFullName(), user.getEmail(), user.getPassword(),user.getRole().name());
    }

    public User toEntity(UserDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setFullName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setRole(Role.valueOf(dto.getRole()));  
        

        return user;
    }
}
