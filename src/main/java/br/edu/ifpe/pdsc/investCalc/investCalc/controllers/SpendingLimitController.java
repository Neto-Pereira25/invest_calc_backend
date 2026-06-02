package br.edu.ifpe.pdsc.investCalc.investCalc.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.ApiResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.monthlySpendingLimit.SpendingLimitResponseDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.monthlySpendingLimit.UpdateSpendingLimitRequestDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.User;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.SpendingLimitService;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/spending-limit")
@RequiredArgsConstructor
public class SpendingLimitController {

    private final SpendingLimitService spendingLimitService;
    private final UserService userService;

    @PostMapping
    public ApiResponse<SpendingLimitResponseDTO> createLimit(
            @RequestBody @Valid UpdateSpendingLimitRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User authenticatedUser = userService.getAuthenticatedUser(userDetails);

        SpendingLimitResponseDTO response = spendingLimitService.createLimit(
                request,
                authenticatedUser);

        return new ApiResponse<>(
                response,
                "Limite mensal criado com sucesso");
    }

    @GetMapping
    public ApiResponse<SpendingLimitResponseDTO> getLimit(
            @AuthenticationPrincipal UserDetails userDetails) {

        User authenticatedUser = userService.getAuthenticatedUser(userDetails);

        SpendingLimitResponseDTO response = spendingLimitService.getLimit(authenticatedUser);

        if (response == null) {
            return new ApiResponse<>(
                    null,
                    "Nenhum limite configurado");
        }

        return new ApiResponse<>(
                response,
                "Limite encontrado com sucesso");
    }

    @PutMapping
    public ApiResponse<SpendingLimitResponseDTO> updateLimit(
            @RequestBody @Valid UpdateSpendingLimitRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User authenticatedUser = userService.getAuthenticatedUser(userDetails);

        SpendingLimitResponseDTO response = spendingLimitService.updateLimit(
                request,
                authenticatedUser);

        return new ApiResponse<>(
                response,
                "Limite atualizado com sucesso");
    }

    @DeleteMapping
    public ApiResponse<Void> deleteLimit(
            @AuthenticationPrincipal UserDetails userDetails) {

        User authenticatedUser = userService.getAuthenticatedUser(userDetails);

        spendingLimitService.deleteLimit(authenticatedUser);

        return new ApiResponse<>(
                null,
                "Limite removido com sucesso");
    }
}
