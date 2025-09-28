package com.example.expensetracker.config;

import com.example.expensetracker.util.constants.Authority;
import com.example.expensetracker.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth

                // Authentication endpoints (public)
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                // ----------------- USER CONTROLLER -----------------
                .requestMatchers(HttpMethod.POST, "/api/users/**").hasAuthority(Authority.ADMIN.name())
                .requestMatchers(HttpMethod.GET, "/api/users/{id}").hasAnyAuthority(Authority.READ.name())
                .requestMatchers(HttpMethod.GET, "/api/users").hasAuthority(Authority.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/api/users/**").hasAuthority(Authority.UPDATE.name())
                .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasAuthority(Authority.DELETE.name())

                // ----------------- EXPENSE CONTROLLER -----------------
                .requestMatchers(HttpMethod.POST, "/api/expenses/**").hasAuthority(Authority.WRITE.name())
                .requestMatchers(HttpMethod.GET, "/api/expenses/**").hasAuthority(Authority.READ.name())
                .requestMatchers(HttpMethod.PUT, "/api/expenses/**").hasAuthority(Authority.UPDATE.name())
                .requestMatchers(HttpMethod.DELETE, "/api/expenses/**").hasAuthority(Authority.DELETE.name())

                // ----------------- CATEGORY CONTROLLER -----------------
                .requestMatchers(HttpMethod.POST, "/api/categories/**").hasAuthority(Authority.WRITE.name())
                .requestMatchers(HttpMethod.GET, "/api/categories/**").hasAuthority(Authority.READ.name())
                .requestMatchers(HttpMethod.PUT, "/api/categories/**").hasAuthority(Authority.UPDATE.name())
                .requestMatchers(HttpMethod.DELETE, "/api/categories/**").hasAuthority(Authority.DELETE.name())

                // fallback
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Add your JWT filter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
