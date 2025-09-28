package com.example.expensetracker.config;

import com.example.expensetracker.dto.CategoryDTO;
import com.example.expensetracker.dto.ExpenseDTO;
import com.example.expensetracker.dto.UserDTO;
import com.example.expensetracker.model.Role;
import com.example.expensetracker.service.CategoryService;
import com.example.expensetracker.service.ExpenseService;
import com.example.expensetracker.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class SeedData implements CommandLineRunner {

    private final UserService userService;
    private final CategoryService categoryService;
    private final ExpenseService expenseService;
    private final PasswordEncoder passwordEncoder;

    public SeedData(UserService userService,
                    CategoryService categoryService,
                    ExpenseService expenseService,
                    PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.categoryService = categoryService;
        this.expenseService = expenseService;
        this.passwordEncoder = passwordEncoder;
    }

   @Override
public void run(String... args) throws Exception {
    // ✅ Users with roles (check before creating)
    if (userService.findByEmail("john@example.com").isEmpty()) {
        UserDTO user1 = UserDTO.builder()
                .name("John Doe")
                .email("john@example.com")
                .password(passwordEncoder.encode("password123"))
                .role(Role.USER.name())
                .build();
        user1 = userService.createUser(user1);
    }

    if (userService.findByEmail("jane@example.com").isEmpty()) {
        UserDTO user2 = UserDTO.builder()
                .name("Jane Smith")
                .email("jane@example.com")
                .password(passwordEncoder.encode("password456"))
                .role(Role.ADMIN.name())
                .build();
        user2 = userService.createUser(user2);
    }

    // ✅ Categories
    if (categoryService.findByName("Food").isEmpty()) {
        CategoryDTO food = CategoryDTO.builder()
                .name("Food")
                .description("Meals and groceries")
                .build();
        categoryService.createCategory(food);
    }

    if (categoryService.findByName("Travel").isEmpty()) {
        CategoryDTO travel = CategoryDTO.builder()
                .name("Travel")
                .description("Trips and commute")
                .build();
        categoryService.createCategory(travel);
    }

    // ✅ Expenses (only seed if none exist, to avoid duplicates)
    if (expenseService.getAllExpenses().isEmpty()) {
        ExpenseDTO expense1 = ExpenseDTO.builder()
                .amount(BigDecimal.valueOf(50.0))
                .description("Lunch")
                .date(LocalDate.now())
                .userId(userService.findByEmail("john@example.com").get().getId())
                .categoryId(categoryService.findByName("Food").get().getId())
                .build();

        ExpenseDTO expense2 = ExpenseDTO.builder()
                .amount(BigDecimal.valueOf(200.0))
                .description("Train ticket")
                .date(LocalDate.now().minusDays(2))
                .userId(userService.findByEmail("jane@example.com").get().getId())
                .categoryId(categoryService.findByName("Travel").get().getId())
                .build();

        expenseService.createExpense(expense1);
        expenseService.createExpense(expense2);
    }

    System.out.println("✅ Seed data inserted (only if missing)!");
}

}
