package com.example.banking.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;  

    @Column(nullable = false)
    private String password;

    private String role = "USER"; 

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Account account;
}
