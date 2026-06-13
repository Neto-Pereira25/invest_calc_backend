package br.edu.ifpe.pdsc.investCalc.investCalc.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.ApiResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.FinancialTransactionRequest;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.FinancialTransactionResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.repeatedExpense.RepeatedExpenseResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.User;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.FinancialTransactionService;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.UserService;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.repeatedExpense.RepeatedExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/financial-transactions")
@RequiredArgsConstructor
public class FinancialTransactionController {

        private final FinancialTransactionService financialTransactionService;
        private final UserService userService;
        private final RepeatedExpenseService repeatedExpenseService;

        @PostMapping
        public ApiResponse<FinancialTransactionResponse> createFinancialTransaction(
                        @Valid @RequestBody FinancialTransactionRequest request,
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

                return ResponseEntity
                                .ok(new ApiResponse<>(transactions, "Transações financeiras listadas com sucessos."));
        }

        @GetMapping("/recurring-expenses")
        public ResponseEntity<ApiResponse<List<RepeatedExpenseResponse>>> listRecurringExpenses(
                        @AuthenticationPrincipal UserDetails userDetails) {

                User user = userService.getAuthenticatedUser(userDetails);

                List<RepeatedExpenseResponse> recurringExpenses = repeatedExpenseService.getRecurringExpenses(user);

                return ResponseEntity.ok(new ApiResponse<>(recurringExpenses,
                                "Gastos recorrentes listados com sucesso."));
        }

        @PutMapping("/{id}")
        public ResponseEntity<ApiResponse<FinancialTransactionResponse>> updateFinancialTransaction(
                        @PathVariable Long id,
                        @Valid @RequestBody FinancialTransactionRequest request,
                        @AuthenticationPrincipal UserDetails userDetails) {
                User user = userService.getAuthenticatedUser(userDetails);

                FinancialTransactionResponse response = financialTransactionService.updateFinancialTransaction(id,
                                request,
                                user);

                return ResponseEntity.ok(new ApiResponse<>(response, "Transação financeira atualizada com sucesso."));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<ApiResponse<Void>> deleteFinancialTransaction(
                        @PathVariable Long id,
                        @AuthenticationPrincipal UserDetails userDetails) {

                User user = userService.getAuthenticatedUser(userDetails);

                financialTransactionService.deleteFinancialTransaction(id, user);

                return ResponseEntity.ok(new ApiResponse<>(null, "Transação financeira deletada com sucesso."));
        }
}
