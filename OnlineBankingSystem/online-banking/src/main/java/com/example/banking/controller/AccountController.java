package com.example.banking.controller;

import com.example.banking.dto.TransferRequest;
import com.example.banking.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "Get account balance by account number")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Balance retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.getBalance(accountNumber.trim()));
    }

    @Operation(summary = "Transfer money between accounts")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transfer successful"),
        @ApiResponse(responseCode = "400", description = "Insufficient balance or invalid accounts")
    })
    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody TransferRequest request) {
        
        String from = request.getFromAccount() == null ? "" : request.getFromAccount().trim();
        String to = request.getToAccount() == null ? "" : request.getToAccount().trim();
        return ResponseEntity.ok(
                accountService.transfer(from, to, request.getAmount())
        );
    }

    @Operation(summary = "Deposit money into your account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Deposit successful"),
        @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PostMapping("/{accountNumber}/deposit")
    public ResponseEntity<String> deposit(
            @PathVariable String accountNumber,
            @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(accountService.deposit(accountNumber.trim(), amount));
    }

    @Operation(summary = "Withdraw money from your account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Withdrawal successful"),
        @ApiResponse(responseCode = "400", description = "Insufficient balance"),
        @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PostMapping("/{accountNumber}/withdraw")
    public ResponseEntity<String> withdraw(
            @PathVariable String accountNumber,
            @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(accountService.withdraw(accountNumber.trim(), amount));
    }
}
