package br.edu.ifpe.pdsc.investCalc.investCalc.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpe.pdsc.investCalc.investCalc.entities.FinancialTransaction;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.User;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.TransactionType;

public interface FinancialTransactionRepository extends JpaRepository<FinancialTransaction, Long> {

    List<FinancialTransaction> findByUser(User user);

    List<FinancialTransaction> findByUserAndTypeAndDateBetween(User user, TransactionType type, LocalDate startDate, LocalDate endDate);
}
