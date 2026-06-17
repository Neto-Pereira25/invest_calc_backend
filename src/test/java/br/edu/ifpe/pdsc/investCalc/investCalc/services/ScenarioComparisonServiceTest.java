package br.edu.ifpe.pdsc.investCalc.investCalc.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.CompoundInterestSimulatorRequest;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.CompoundInterestSimulatorResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.scenarioComparison.CompareScenariosRequest;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.scenarioComparison.ScenarioComparisonResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.scenarioComparison.ScenarioRequest;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.PeriodType;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.RateType;

@ExtendWith(MockitoExtension.class)
class ScenarioComparisonServiceTest {

    @Mock
    private CompoundInterestSimulatorService compoundInterestSimulatorService;

    @InjectMocks
    private ScenarioComparisonService scenarioComparisonService;

    @Test
    void shouldCompareScenariosAndKeepInputOrder() {

        CompareScenariosRequest request = new CompareScenariosRequest(List.of(
                new ScenarioRequest("Cenário A", BigDecimal.valueOf(1000), BigDecimal.valueOf(200), BigDecimal.ONE,
                        120),
                new ScenarioRequest("Cenário B", BigDecimal.valueOf(1000), BigDecimal.valueOf(400), BigDecimal.ONE,
                        120)));

        when(compoundInterestSimulatorService.simulate(any()))
                .thenReturn(new CompoundInterestSimulatorResponse(
                        BigDecimal.valueOf(25000),
                        BigDecimal.valueOf(18000),
                        BigDecimal.valueOf(43000)))
                .thenReturn(new CompoundInterestSimulatorResponse(
                        BigDecimal.valueOf(49000),
                        BigDecimal.valueOf(35000),
                        BigDecimal.valueOf(84000)));

        List<ScenarioComparisonResponse> response = scenarioComparisonService.compareScenarios(request);

        assertEquals(2, response.size());
        assertEquals("Cenário A", response.get(0).scenarioName());
        assertEquals(BigDecimal.valueOf(43000), response.get(0).finalAmount());
        assertEquals("Cenário B", response.get(1).scenarioName());
        assertEquals(BigDecimal.valueOf(84000), response.get(1).finalAmount());
    }

    @Test
    void shouldMapScenarioRequestToMonthlySimulationRequest() {

        CompareScenariosRequest request = new CompareScenariosRequest(List.of(
                new ScenarioRequest("Cenário Único", BigDecimal.valueOf(1500), BigDecimal.valueOf(300),
                        BigDecimal.valueOf(1.5), 24),
                new ScenarioRequest("Cenário Apoio", BigDecimal.valueOf(1200), BigDecimal.valueOf(100),
                        BigDecimal.valueOf(0.8), 12)));

        when(compoundInterestSimulatorService.simulate(any()))
                .thenReturn(new CompoundInterestSimulatorResponse(BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE))
                .thenReturn(new CompoundInterestSimulatorResponse(BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE));

        scenarioComparisonService.compareScenarios(request);

        ArgumentCaptor<CompoundInterestSimulatorRequest> captor = ArgumentCaptor
                .forClass(CompoundInterestSimulatorRequest.class);

        verify(compoundInterestSimulatorService, times(2)).simulate(captor.capture());

        CompoundInterestSimulatorRequest firstSimulationRequest = captor.getAllValues().get(0);

        assertEquals(BigDecimal.valueOf(1500), firstSimulationRequest.getInitialValue());
        assertEquals(BigDecimal.valueOf(300), firstSimulationRequest.getMonthlyContribution());
        assertEquals(BigDecimal.valueOf(1.5), firstSimulationRequest.getInterestRate());
        assertEquals(24, firstSimulationRequest.getPeriod());
        assertEquals(PeriodType.MONTHLY, firstSimulationRequest.getPeriodType());
        assertEquals(RateType.MONTHLY, firstSimulationRequest.getRateType());
    }
}
