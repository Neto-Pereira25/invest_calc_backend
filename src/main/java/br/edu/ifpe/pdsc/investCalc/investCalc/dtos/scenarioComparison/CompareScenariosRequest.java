package br.edu.ifpe.pdsc.investCalc.investCalc.dtos.scenarioComparison;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CompareScenariosRequest(
        @NotNull(message = "Lista de cenários é obrigatória") @Size(min = 2, message = "É necessário informar pelo menos dois cenários") List<@Valid ScenarioRequest> scenarios) {
}
