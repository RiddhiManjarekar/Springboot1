package com.example.banking.service;

import com.example.banking.model.Account;
import com.example.banking.model.Transaction;
import com.example.banking.repository.AccountRepository;
import com.example.banking.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public BigDecimal getBalance(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return account.getBalance();
    }

    public String transfer(String fromAcc, String toAcc, BigDecimal amount) {
        Account from = accountRepository.findByAccountNumber(fromAcc)
                .orElseThrow(() -> new RuntimeException("Sender account not found"));
        Account to = accountRepository.findByAccountNumber(toAcc)
                .orElseThrow(() -> new RuntimeException("Receiver account not found"));

        if (from.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        
        from.setBalance(from.getBalance().subtract(amount));
        accountRepository.save(from);
        recordTransaction(from, amount, "TRANSFER_DEBIT");

       
        to.setBalance(to.getBalance().add(amount));
        accountRepository.save(to);
        recordTransaction(to, amount, "TRANSFER_CREDIT");

        return "Transfer successful";
    }

    public String deposit(String accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        recordTransaction(account, amount, "DEPOSIT");

        return "Amount deposited successfully";
    }

    public String withdraw(String accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        recordTransaction(account, amount, "WITHDRAW");

        return "Amount withdrawn successfully";
    }

    private void recordTransaction(Account account, BigDecimal amount, String type) {
        Transaction tx = Transaction.builder()
                .account(account)
                .amount(amount)
                .type(type)
                .timestamp(LocalDateTime.now())
                .build();
        transactionRepository.save(tx);
    }
}
