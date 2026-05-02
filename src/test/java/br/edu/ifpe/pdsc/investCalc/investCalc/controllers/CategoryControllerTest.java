package br.edu.ifpe.pdsc.investCalc.investCalc.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.CategoryResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.SubcategoryResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.GlobalExceptionHandler;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.CustomUserDetailsService;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.JwtAuthenticationFilter;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.JwtService;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.AuthService;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.CategoryService;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtService jwtService;

    @Test
    void shouldReturnCategories() throws Exception {

        List<CategoryResponse> mockResponse = List.of(
                new CategoryResponse(
                        1L,
                        "Alimentação",
                        "EXPENSE",
                        List.of(new SubcategoryResponse(1L, "Restaurante"))));

        when(categoryService.findAll()).thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("Alimentação"))
                .andExpect(jsonPath("$.data[0].subcategories[0].name").value("Restaurante"));
    }
}
