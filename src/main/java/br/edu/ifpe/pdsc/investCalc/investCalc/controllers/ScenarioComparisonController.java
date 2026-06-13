package br.edu.ifpe.pdsc.investCalc.investCalc.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.ApiResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.scenarioComparison.CompareScenariosRequest;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.scenarioComparison.ScenarioComparisonResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.ScenarioComparisonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/simulations")
@RequiredArgsConstructor
public class ScenarioComparisonController {

    private final ScenarioComparisonService scenarioComparisonService;

    @PostMapping("/compare")
    public ResponseEntity<ApiResponse<List<ScenarioComparisonResponse>>> compare(
            @Valid @RequestBody CompareScenariosRequest request) {

        List<ScenarioComparisonResponse> response = scenarioComparisonService.compareScenarios(request);

        return ResponseEntity.ok(new ApiResponse<>(response, "Comparação de cenários realizada com sucesso"));
    }
}
