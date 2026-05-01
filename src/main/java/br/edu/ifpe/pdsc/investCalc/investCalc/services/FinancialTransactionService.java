package br.edu.ifpe.pdsc.investCalc.investCalc.services;

import org.springframework.stereotype.Service;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.CreateFinancialTransactionRequest;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.FinancialTransaction;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.Subcategory;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.User;
import br.edu.ifpe.pdsc.investCalc.investCalc.repositories.FinancialTransactionRepository;
import br.edu.ifpe.pdsc.investCalc.investCalc.repositories.SubcategoryRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FinancialTransactionService {

    private final FinancialTransactionRepository transactionRepository;
    private final SubcategoryRepository subcategoryRepository;

    public FinancialTransaction createTransaction(CreateFinancialTransactionRequest request, User user) {

        // 1. Buscar subcategoria
        Subcategory subcategory = subcategoryRepository
                .findById(request.getSubcategoryId())
                .orElseThrow(() -> new RuntimeException("Subcategoria não encontrada"));

        // 2. Criar entidade
        FinancialTransaction transaction = new FinancialTransaction();
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setDate(request.getDate());

        // 🔥 IMPORTANTE (baseado no seu domínio atualizado)
        transaction.setSubcategory(subcategory);

        // Associação com usuário
        transaction.setUser(user);

        // 3. Salvar
        return transactionRepository.save(transaction);
    }
}
