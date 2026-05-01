package br.edu.ifpe.pdsc.investCalc.investCalc.services;

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

        // 1. Buscar subcategoria
        Subcategory subcategory = subcategoryRepository
                .findById(request.getSubcategoryId())
                .orElseThrow(() -> new RuntimeException("Subcategoria não encontrada"));

        TransactionType type = subcategory.getCategory().getType();

        // 2. Criar entidade
        FinancialTransaction transaction = new FinancialTransaction();
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setDate(request.getDate());
        transaction.setSubcategory(subcategory);
        transaction.setUser(user);
        transaction.setType(type);

        // 3. Salvar
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
}
