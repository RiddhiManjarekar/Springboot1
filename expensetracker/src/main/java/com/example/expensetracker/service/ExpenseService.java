package com.example.expensetracker.service;

import com.example.expensetracker.dto.ExpenseDTO;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseService {
    ExpenseDTO createExpense(ExpenseDTO expenseDTO);
    ExpenseDTO getExpenseById(Long id);
    List<ExpenseDTO> getAllExpenses();
    List<ExpenseDTO> getExpensesByUser(Long userId);
    List<ExpenseDTO> getExpensesByCategory(Long categoryId);
    List<ExpenseDTO> getExpensesByDateRange(Long userId, LocalDate start, LocalDate end);
    ExpenseDTO updateExpense(Long id, ExpenseDTO expenseDTO);
    void deleteExpense(Long id);
}
