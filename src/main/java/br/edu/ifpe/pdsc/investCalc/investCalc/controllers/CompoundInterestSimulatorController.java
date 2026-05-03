package br.edu.ifpe.pdsc.investCalc.investCalc.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.ApiResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.CompoundInterestSimulatorRequest;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.CompoundInterestSimulatorResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.CompoundInterestSimulatorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/compound-interest-simulator")
@RequiredArgsConstructor
public class CompoundInterestSimulatorController {

    private final CompoundInterestSimulatorService simulationService;

    @PostMapping
    public ResponseEntity<ApiResponse<CompoundInterestSimulatorResponse>> simulate(
            @Valid @RequestBody CompoundInterestSimulatorRequest request) {

        CompoundInterestSimulatorResponse response = simulationService.simulate(request);

        return ResponseEntity.ok(
                new ApiResponse<>(response, "Simulação realizada com sucesso"));
    }
}
