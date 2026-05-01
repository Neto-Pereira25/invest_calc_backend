package br.edu.ifpe.pdsc.investCalc.investCalc.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.ApiResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.FinancialTransactionRequest;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.FinancialTransactionResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.User;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.FinancialTransactionService;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/financial-transactions")
@RequiredArgsConstructor
public class FinancialTransactionController {

    private final FinancialTransactionService financialTransactionService;
    private final UserService userService;

    @PostMapping
    public ApiResponse<FinancialTransactionResponse> createFinancialTransaction(
            @RequestBody FinancialTransactionRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userService.getAuthenticatedUser(userDetails);

        FinancialTransactionResponse createdTransaction = financialTransactionService
                .createFinancialTransaction(request, user);

        return new ApiResponse<>(createdTransaction, "Transação financeira criada com sucesso.");
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FinancialTransactionResponse>>> listFinancialTransactions(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userService.getAuthenticatedUser(userDetails);

        List<FinancialTransactionResponse> transactions = financialTransactionService
                .listByUser(user);

        return ResponseEntity.ok(new ApiResponse<>(transactions, "Transações financeiras listadas com sucessos."));
    }
}
