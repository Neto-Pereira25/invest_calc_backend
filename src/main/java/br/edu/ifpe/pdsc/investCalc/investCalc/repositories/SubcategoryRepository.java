package br.edu.ifpe.pdsc.investCalc.investCalc.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpe.pdsc.investCalc.investCalc.entities.Category;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.Subcategory;

public interface SubcategoryRepository extends JpaRepository<Subcategory, UUID> {

    List<Subcategory> findByCategory(Category category);
}
