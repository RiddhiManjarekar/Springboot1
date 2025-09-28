package com.example.expensetracker.mapper;

import com.example.expensetracker.dto.ExpenseDTO;
import com.example.expensetracker.model.Category;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.User;
import org.springframework.stereotype.Component;

@Component
public class ExpenseMapper {
    public ExpenseDTO toDTO(Expense expense) {
        return new ExpenseDTO(
                expense.getId(),
                expense.getAmount(),
                expense.getDescription(),
                expense.getDate(),
                expense.getUser().getId(),
                expense.getCategory().getId()
        );
    }

    public Expense toEntity(ExpenseDTO dto, User user, Category category) {
        Expense expense = new Expense();
        expense.setId(dto.getId());
        expense.setAmount(dto.getAmount());
        expense.setDescription(dto.getDescription());
        expense.setDate(dto.getDate());
        expense.setUser(user);
        expense.setCategory(category);
        return expense;
    }
}
