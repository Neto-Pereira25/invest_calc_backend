package br.edu.ifpe.pdsc.investCalc.investCalc.dtos;

import java.util.List;

public record CategoryResponse(
        Long id,
        String name,
        String type,
        List<SubcategoryResponse> subcategories) {
}
