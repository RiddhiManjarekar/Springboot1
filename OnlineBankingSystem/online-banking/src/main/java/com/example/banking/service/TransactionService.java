package com.example.banking.service;

import com.example.banking.dto.TransactionDTO;
import com.example.banking.model.Account;
import com.example.banking.model.Transaction;
import com.example.banking.repository.AccountRepository;
import com.example.banking.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

   
    public List<TransactionDTO> getTransactions(String accountNumber, String type, LocalDate start, LocalDate end) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        List<Transaction> transactions;

        if (start != null && end != null) {
            LocalDateTime startDt = start.atStartOfDay();
            LocalDateTime endDt = end.plusDays(1).atStartOfDay(); 
            transactions = transactionRepository.findByAccountIdAndTimestampBetween(account.getId(), startDt, endDt);
        } else if (type != null && !type.isBlank()) {
            transactions = transactionRepository.findByAccountIdAndType(account.getId(), type);
        } else {
            transactions = transactionRepository.findByAccountId(account.getId());
        }

        return transactions.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private TransactionDTO toDTO(Transaction tx) {
        return TransactionDTO.builder()
                .id(tx.getId())
                .amount(tx.getAmount())
                .type(tx.getType())
                .timestamp(tx.getTimestamp())
                .accountNumber(tx.getAccount().getAccountNumber())
                .build();
    }
}
