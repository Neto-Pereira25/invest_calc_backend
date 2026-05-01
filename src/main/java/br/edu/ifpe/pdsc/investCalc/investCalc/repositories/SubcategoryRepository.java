package br.edu.ifpe.pdsc.investCalc.investCalc.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpe.pdsc.investCalc.investCalc.entities.Category;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.Subcategory;

public interface SubcategoryRepository extends JpaRepository<Subcategory, Long> {

    List<Subcategory> findByCategory(Category category);
}
