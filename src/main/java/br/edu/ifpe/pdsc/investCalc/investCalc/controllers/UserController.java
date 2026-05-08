package br.edu.ifpe.pdsc.investCalc.investCalc.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.ApiResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.UserResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ApiResponse<UserResponse> getAuthenticatedUser(@AuthenticationPrincipal UserDetails userDetails) {
        var user = userService.getAuthenticatedUser(userDetails);
        return new ApiResponse<>(UserResponse.from(user), "Dados do usuário retornados com sucesso");
    }
}
