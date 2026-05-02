package br.edu.ifpe.pdsc.investCalc.investCalc.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.ApiResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.CategoryResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.CategoryService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> findAll() {

        List<CategoryResponse> categories = categoryService.findAll();

        return ResponseEntity.ok(new ApiResponse<>(categories, "Categorias listadas com sucesso"));
    }
}
