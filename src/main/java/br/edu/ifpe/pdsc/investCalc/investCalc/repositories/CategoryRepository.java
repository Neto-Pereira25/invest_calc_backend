package br.edu.ifpe.pdsc.investCalc.investCalc.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpe.pdsc.investCalc.investCalc.entities.Category;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.TransactionType;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findByType(TransactionType type);
}
