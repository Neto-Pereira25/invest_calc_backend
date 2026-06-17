package br.edu.ifpe.pdsc.investCalc.investCalc.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.ApiResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.userFinancialProfile.FinancialProfileHistoryDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.userFinancialProfile.FinancialProfileRequestDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.userFinancialProfile.FinancialProfileResponseDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.User;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.UserService;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.userFinancialProfile.FinancialProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/financial-profile")
@RequiredArgsConstructor
public class FinancialProfileController {

    private final FinancialProfileService financialProfileService;
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<FinancialProfileResponseDTO> submitAssessment(
            @Valid @RequestBody FinancialProfileRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User authenticatedUser = userService.getAuthenticatedUser(userDetails);

        FinancialProfileResponseDTO response = financialProfileService.submitAssessment(
                request,
                authenticatedUser);

        return new ApiResponse<>(
                response,
                "Financial profile successfully calculated.");
    }

    @GetMapping
    public ApiResponse<FinancialProfileResponseDTO> getCurrentProfile(
            @AuthenticationPrincipal UserDetails userDetails) {

        User authenticatedUser = userService.getAuthenticatedUser(userDetails);

        FinancialProfileResponseDTO response = financialProfileService.getCurrentProfile(
                authenticatedUser);

        return new ApiResponse<>(
                response,
                "Financial profile successfully retrieved.");
    }

    @GetMapping("/history")
    public ApiResponse<List<FinancialProfileHistoryDTO>> getHistory(
            @AuthenticationPrincipal UserDetails userDetails) {

        User authenticatedUser = userService.getAuthenticatedUser(userDetails);

        List<FinancialProfileHistoryDTO> response = financialProfileService.getHistory(
                authenticatedUser);

        return new ApiResponse<>(
                response,
                "Financial profile history successfully retrieved.");
    }
}
