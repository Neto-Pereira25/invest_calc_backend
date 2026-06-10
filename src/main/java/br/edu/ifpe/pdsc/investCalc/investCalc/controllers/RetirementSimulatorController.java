package br.edu.ifpe.pdsc.investCalc.investCalc.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.ApiResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.RetirementSimulatorRequest;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.RetirementSimulatorResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.RetirementSimulatorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/retirement-simulator")
@RequiredArgsConstructor
public class RetirementSimulatorController {

    private final RetirementSimulatorService retirementSimulatorService;

    @PostMapping
    public ResponseEntity<ApiResponse<RetirementSimulatorResponse>> simulate(
            @Valid @RequestBody RetirementSimulatorRequest request) {

        RetirementSimulatorResponse response = retirementSimulatorService.simulate(request);

        return ResponseEntity.ok(
                new ApiResponse<>(response, "Simulação realizada com sucesso"));
    }
}
