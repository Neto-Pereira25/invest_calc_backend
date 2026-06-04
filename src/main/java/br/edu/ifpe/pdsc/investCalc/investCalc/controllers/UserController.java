package br.edu.ifpe.pdsc.investCalc.investCalc.controllers;

import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.ApiResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.FinancialSummaryDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.UpdateUserNameRequest;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.UserResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.FinancialSummaryService;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FinancialSummaryService financialSummaryService;

    @GetMapping("/profile")
    public ApiResponse<UserResponse> getAuthenticatedUser(@AuthenticationPrincipal UserDetails userDetails) {
        var user = userService.getAuthenticatedUser(userDetails);
        return new ApiResponse<>(UserResponse.from(user), "Dados do usuário retornados com sucesso");
    }

    @PatchMapping("/profile")
    public ApiResponse<UserResponse> updateAuthenticatedUser(@AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateUserNameRequest request) {
        var user = userService.updateAuthenticatedUserName(userDetails, request.name());
        return new ApiResponse<>(UserResponse.from(user), "Nome do usuário atualizado com sucesso");
    }

    @GetMapping("/financial-summary")
    public ApiResponse<FinancialSummaryDTO> getFinancialSummary(@AuthenticationPrincipal UserDetails userDetails) {
        var user = userService.getAuthenticatedUser(userDetails);
        var summary = financialSummaryService.getFinancialSummary(user);
        return new ApiResponse<>(summary, "Resumo financeiro carregado com sucesso");
    }
}
