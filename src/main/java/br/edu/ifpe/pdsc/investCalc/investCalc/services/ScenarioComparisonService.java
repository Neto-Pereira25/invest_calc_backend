package br.edu.ifpe.pdsc.investCalc.investCalc.services;

import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.CompoundInterestSimulatorRequest;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.CompoundInterestSimulatorResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.scenarioComparison.CompareScenariosRequest;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.scenarioComparison.ScenarioComparisonResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.scenarioComparison.ScenarioRequest;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.PeriodType;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.RateType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScenarioComparisonService {

    private final CompoundInterestSimulatorService compoundInterestSimulatorService;

    public List<ScenarioComparisonResponse> compareScenarios(CompareScenariosRequest request) {

        return request.scenarios().stream()
                .map(this::simulateScenario)
                .toList();
    }

    private ScenarioComparisonResponse simulateScenario(ScenarioRequest scenario) {

        CompoundInterestSimulatorRequest simulationRequest = new CompoundInterestSimulatorRequest(
                scenario.initialCapital(),
                scenario.monthlyContribution(),
                scenario.interestRate(),
                scenario.months(),
                PeriodType.MONTHLY,
                RateType.MONTHLY);

        CompoundInterestSimulatorResponse simulationResponse = compoundInterestSimulatorService
                .simulate(simulationRequest);

        return new ScenarioComparisonResponse(
                scenario.name(),
                simulationResponse.getTotalInvested(),
                simulationResponse.getTotalInterest(),
                simulationResponse.getFinalAmount());
    }
}
