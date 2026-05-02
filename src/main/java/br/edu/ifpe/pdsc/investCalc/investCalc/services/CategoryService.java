package br.edu.ifpe.pdsc.investCalc.investCalc.services;

import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.CategoryResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.SubcategoryResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> findAll() {

        return categoryRepository.findAll()
                .stream()
                .map(category -> new CategoryResponse(
                        category.getId(),
                        category.getName(),
                        category.getType().name(),
                        category.getSubcategories()
                                .stream()
                                .map(sub -> new SubcategoryResponse(
                                        sub.getId(),
                                        sub.getName()))
                                .toList()))
                .toList();
    }
}
