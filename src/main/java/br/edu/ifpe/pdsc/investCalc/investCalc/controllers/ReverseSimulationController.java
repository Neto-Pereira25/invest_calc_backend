package br.edu.ifpe.pdsc.investCalc.investCalc.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.ApiResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.ReverseSimulationRequest;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.ReverseSimulationResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.ReverseSimulationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/reverse-simulation")
@RequiredArgsConstructor
public class ReverseSimulationController {

    private final ReverseSimulationService reverseSimulationService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReverseSimulationResponse>> simulate(
            @Valid @RequestBody ReverseSimulationRequest request) {

        ReverseSimulationResponse response = reverseSimulationService.simulate(request);

        return ResponseEntity.ok(
                new ApiResponse<>(response, "Simulação reversa realizada com sucesso"));
    }
}
