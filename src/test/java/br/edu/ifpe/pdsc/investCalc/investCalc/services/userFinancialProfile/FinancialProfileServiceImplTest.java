package br.edu.ifpe.pdsc.investCalc.investCalc.services.userFinancialProfile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.userFinancialProfile.FinancialProfileAnswerRequestDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.userFinancialProfile.FinancialProfileHistoryDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.userFinancialProfile.FinancialProfileRequestDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.userFinancialProfile.FinancialProfileResponseDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.User;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.userFinancialProfile.FinancialProfileAnswer;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.userFinancialProfile.FinancialProfileResult;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.userFinancialProfile.FinancialProfile;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.userFinancialProfile.FinancialProfileOption;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.userFinancialProfile.FinancialProfileNotFoundException;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.userFinancialProfile.InvalidFinancialProfileAssessmentException;
import br.edu.ifpe.pdsc.investCalc.investCalc.repositories.userFinancialProfile.FinancialProfileResultRepository;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.userFinancialProfile.mapper.FinancialProfileMapper;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.userFinancialProfile.rules.FinancialProfileRuleEngine;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.userFinancialProfile.rules.FinancialProfileScore;

@ExtendWith(MockitoExtension.class)
class FinancialProfileServiceImplTest {

    @Mock
    private FinancialProfileResultRepository repository;

    @Mock
    private FinancialProfileRuleEngine ruleEngine;

    @Mock
    private FinancialProfileMapper mapper;

    @InjectMocks
    private FinancialProfileServiceImpl service;

    private User authenticatedUser;

    @BeforeEach
    void setup() {
        authenticatedUser = User.builder()
                .id(1L)
                .email("user@email.com")
                .build();
    }

    @Test
    @DisplayName("Should submit assessment and return mapped response")
    void shouldSubmitAssessmentAndReturnMappedResponse() {
        FinancialProfileRequestDTO request = requestWithTenAnswers();

        FinancialProfileScore score = new FinancialProfileScore();
        score.add(FinancialProfile.DEVEDOR, 2);
        score.add(FinancialProfile.GASTADOR, 3);
        score.add(FinancialProfile.DESLIGADO, 1);
        score.add(FinancialProfile.POUPADOR, 6);
        score.add(FinancialProfile.INVESTIDOR, 8);

        FinancialProfileResult savedResult = FinancialProfileResult.builder()
                .id(50L)
                .profile(FinancialProfile.INVESTIDOR)
                .devedorScore(2)
                .gastadorScore(3)
                .desligadoScore(1)
                .poupadorScore(6)
                .investidorScore(8)
                .assessedAt(LocalDateTime.now())
                .user(authenticatedUser)
                .build();

        FinancialProfileResponseDTO expectedResponse = FinancialProfileResponseDTO.builder()
                .profile(FinancialProfile.INVESTIDOR)
                .build();

        when(ruleEngine.calculate(request.getAnswers())).thenReturn(score);
        when(repository.save(any(FinancialProfileResult.class))).thenReturn(savedResult);
        when(mapper.toResponseDTO(savedResult)).thenReturn(expectedResponse);

        FinancialProfileResponseDTO response = service.submitAssessment(request, authenticatedUser);

        assertNotNull(response);
        assertEquals(FinancialProfile.INVESTIDOR, response.getProfile());

        ArgumentCaptor<FinancialProfileResult> captor = ArgumentCaptor.forClass(FinancialProfileResult.class);
        verify(repository, times(1)).save(captor.capture());

        FinancialProfileResult persisted = captor.getValue();
        assertEquals(authenticatedUser, persisted.getUser());
        assertEquals(FinancialProfile.INVESTIDOR, persisted.getProfile());
        assertEquals(10, persisted.getAnswers().size());

        FinancialProfileAnswer firstAnswer = persisted.getAnswers().get(0);
        assertEquals(1, firstAnswer.getQuestionNumber());
        assertEquals(FinancialProfileOption.A, firstAnswer.getSelectedOption());
        assertEquals(persisted, firstAnswer.getResult());
    }

    @Test
    @DisplayName("Should throw exception when questionnaire size is different from ten")
    void shouldThrowExceptionWhenQuestionnaireSizeIsDifferentFromTen() {
        FinancialProfileRequestDTO request = new FinancialProfileRequestDTO();
        request.setAnswers(List.of(answer(1, FinancialProfileOption.A)));

        InvalidFinancialProfileAssessmentException exception = assertThrows(
                InvalidFinancialProfileAssessmentException.class,
                () -> service.submitAssessment(request, authenticatedUser));

        assertEquals("O questionario deve conter exatamente 10 respostas.", exception.getMessage());
        verify(ruleEngine, never()).calculate(any());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should return current profile when result exists")
    void shouldReturnCurrentProfileWhenResultExists() {
        FinancialProfileResult result = FinancialProfileResult.builder()
                .id(10L)
                .profile(FinancialProfile.POUPADOR)
                .build();

        FinancialProfileResponseDTO expectedResponse = FinancialProfileResponseDTO.builder()
                .profile(FinancialProfile.POUPADOR)
                .build();

        when(repository.findTopByUserOrderByAssessedAtDesc(authenticatedUser)).thenReturn(Optional.of(result));
        when(mapper.toResponseDTO(result)).thenReturn(expectedResponse);

        FinancialProfileResponseDTO response = service.getCurrentProfile(authenticatedUser);

        assertNotNull(response);
        assertEquals(FinancialProfile.POUPADOR, response.getProfile());
    }

    @Test
    @DisplayName("Should throw exception when current profile is not found")
    void shouldThrowExceptionWhenCurrentProfileIsNotFound() {
        when(repository.findTopByUserOrderByAssessedAtDesc(authenticatedUser)).thenReturn(Optional.empty());

        FinancialProfileNotFoundException exception = assertThrows(
                FinancialProfileNotFoundException.class,
                () -> service.getCurrentProfile(authenticatedUser));

        assertEquals("Perfil financeiro nao encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Should return mapped history ordered by repository response")
    void shouldReturnMappedHistoryOrderedByRepositoryResponse() {
        FinancialProfileResult older = FinancialProfileResult.builder().id(1L).profile(FinancialProfile.GASTADOR)
                .build();
        FinancialProfileResult newer = FinancialProfileResult.builder().id(2L).profile(FinancialProfile.INVESTIDOR)
                .build();

        FinancialProfileHistoryDTO olderDto = FinancialProfileHistoryDTO.builder().id(1L)
                .profile(FinancialProfile.GASTADOR).build();
        FinancialProfileHistoryDTO newerDto = FinancialProfileHistoryDTO.builder().id(2L)
                .profile(FinancialProfile.INVESTIDOR).build();

        when(repository.findByUserOrderByAssessedAtDesc(authenticatedUser)).thenReturn(List.of(newer, older));
        when(mapper.toHistoryDTO(newer)).thenReturn(newerDto);
        when(mapper.toHistoryDTO(older)).thenReturn(olderDto);

        List<FinancialProfileHistoryDTO> history = service.getHistory(authenticatedUser);

        assertEquals(2, history.size());
        assertEquals(2L, history.get(0).getId());
        assertEquals(1L, history.get(1).getId());
    }

    private FinancialProfileRequestDTO requestWithTenAnswers() {
        FinancialProfileRequestDTO request = new FinancialProfileRequestDTO();
        request.setAnswers(List.of(
                answer(1, FinancialProfileOption.A),
                answer(2, FinancialProfileOption.B),
                answer(3, FinancialProfileOption.C),
                answer(4, FinancialProfileOption.D),
                answer(5, FinancialProfileOption.E),
                answer(6, FinancialProfileOption.A),
                answer(7, FinancialProfileOption.B),
                answer(8, FinancialProfileOption.C),
                answer(9, FinancialProfileOption.D),
                answer(10, FinancialProfileOption.E)));
        return request;
    }

    private FinancialProfileAnswerRequestDTO answer(Integer questionNumber, FinancialProfileOption option) {
        FinancialProfileAnswerRequestDTO answer = new FinancialProfileAnswerRequestDTO();
        answer.setQuestionNumber(questionNumber);
        answer.setSelectedOption(option);
        return answer;
    }
}
