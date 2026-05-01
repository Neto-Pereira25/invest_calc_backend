package br.edu.ifpe.pdsc.investCalc.investCalc.services;

import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.FinancialTransactionRequest;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.FinancialTransactionResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.FinancialTransaction;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.Subcategory;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.User;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.TransactionType;
import br.edu.ifpe.pdsc.investCalc.investCalc.repositories.FinancialTransactionRepository;
import br.edu.ifpe.pdsc.investCalc.investCalc.repositories.SubcategoryRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FinancialTransactionService {

    private final FinancialTransactionRepository transactionRepository;
    private final SubcategoryRepository subcategoryRepository;

    public FinancialTransactionResponse createFinancialTransaction(FinancialTransactionRequest request, User user) {

        Subcategory subcategory = subcategoryRepository
                .findById(request.getSubcategoryId())
                .orElseThrow(() -> new RuntimeException("Subcategoria não encontrada"));

        TransactionType type = subcategory.getCategory().getType();

        FinancialTransaction transaction = new FinancialTransaction();
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setDate(request.getDate());
        transaction.setSubcategory(subcategory);
        transaction.setUser(user);
        transaction.setType(type);

        transactionRepository.save(transaction);

        return new FinancialTransactionResponse(
                transaction.getId(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getSubcategory().getCategory().getName(),
                transaction.getSubcategory().getName(),
                transaction.getDate());
    }

    public List<FinancialTransactionResponse> listByUser(User user) {

        List<FinancialTransaction> transactions = transactionRepository.findByUser(user);

        return transactions.stream()
                .map(transaction -> new FinancialTransactionResponse(
                        transaction.getId(),
                        transaction.getDescription(),
                        transaction.getAmount(),
                        transaction.getType(),
                        transaction.getSubcategory().getCategory().getName(),
                        transaction.getSubcategory().getName(),
                        transaction.getDate()))
                .toList();
    }
}
