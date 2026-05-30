package br.edu.ifpe.pdsc.investCalc.investCalc.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.goals.CreateGoalRequestDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.goals.GoalResponseDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.goals.UpdateGoalProgressRequestDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.goals.UpdateGoalRequestDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.Goal;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.User;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.GoalStatus;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.goals.GoalNotFoundException;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.goals.UnauthorizedGoalAccessException;
import br.edu.ifpe.pdsc.investCalc.investCalc.repositories.GoalRepository;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private GoalService goalService;

    private User authenticatedUser;
    private Goal goal;
    private Long goalId;

    @BeforeEach
    void setup() {
        goalId = 10L;

        authenticatedUser = new User();
        authenticatedUser.setId(1L);

        goal = new Goal();
        goal.setId(goalId);
        goal.setName("Reserva de emergência");
        goal.setTargetAmount(BigDecimal.valueOf(10000));
        goal.setCurrentAmount(BigDecimal.valueOf(2000));
        goal.setDeadline(LocalDate.now().plusMonths(6));
        goal.setStatus(GoalStatus.ACTIVE);
        goal.setCreatedAt(LocalDateTime.now());
        goal.setUser(authenticatedUser);
    }

    @Test
    void shouldCreateGoalSuccessfully() {
        CreateGoalRequestDTO request = new CreateGoalRequestDTO();
        request.setName("Viagem");
        request.setTargetAmount(BigDecimal.valueOf(5000));
        request.setDeadline(LocalDate.now().plusMonths(8));

        when(goalRepository.save(any(Goal.class))).thenAnswer(invocation -> {
            Goal saved = invocation.getArgument(0);
            saved.setId(1L);
            saved.setCurrentAmount(BigDecimal.ZERO);
            saved.setStatus(GoalStatus.ACTIVE);
            saved.setCreatedAt(LocalDateTime.now());
            return saved;
        });

        GoalResponseDTO result = goalService.createGoal(request, authenticatedUser);

        assertEquals("Viagem", result.getName());
        assertEquals(BigDecimal.valueOf(5000), result.getTargetAmount());
        assertEquals(BigDecimal.ZERO, result.getCurrentAmount());
        assertEquals(GoalStatus.ACTIVE, result.getStatus());
        assertEquals(BigDecimal.ZERO.setScale(2), result.getProgressPercentage());
    }

    @Test
    void shouldListGoalsByUser() {
        when(goalRepository.findByUser(authenticatedUser)).thenReturn(List.of(goal));

        List<GoalResponseDTO> result = goalService.getUserGoals(authenticatedUser);

        assertEquals(1, result.size());
        assertEquals(goal.getName(), result.get(0).getName());
        assertEquals(goal.getTargetAmount(), result.get(0).getTargetAmount());
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoGoals() {
        when(goalRepository.findByUser(authenticatedUser)).thenReturn(List.of());

        List<GoalResponseDTO> result = goalService.getUserGoals(authenticatedUser);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldGetGoalByIdSuccessfully() {
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));

        GoalResponseDTO result = goalService.getGoalById(goalId, authenticatedUser);

        assertEquals(goalId, result.getId());
        assertEquals("Reserva de emergência", result.getName());
    }

    @Test
    void shouldThrowExceptionWhenGoalNotFound() {
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());

        GoalNotFoundException exception = assertThrows(
                GoalNotFoundException.class,
                () -> goalService.getGoalById(goalId, authenticatedUser));

        assertEquals("Meta financeira não encontrada", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotGoalOwner() {
        User anotherUser = new User();
        anotherUser.setId(999L);
        goal.setUser(anotherUser);

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));

        UnauthorizedGoalAccessException exception = assertThrows(
                UnauthorizedGoalAccessException.class,
                () -> goalService.getGoalById(goalId, authenticatedUser));

        assertEquals("Voce nao possui permissao para acessar esta meta financeira", exception.getMessage());
    }

    @Test
    void shouldUpdateGoalSuccessfully() {
        UpdateGoalRequestDTO request = new UpdateGoalRequestDTO();
        request.setName("Novo objetivo");
        request.setTargetAmount(BigDecimal.valueOf(12000));
        request.setDeadline(LocalDate.now().plusMonths(10));

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));
        when(goalRepository.save(any(Goal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GoalResponseDTO result = goalService.updateGoal(goalId, request, authenticatedUser);

        assertEquals("Novo objetivo", result.getName());
        assertEquals(BigDecimal.valueOf(12000), result.getTargetAmount());
        assertEquals(GoalStatus.ACTIVE, result.getStatus());
    }

    @Test
    void shouldUpdateGoalProgressSuccessfully() {
        UpdateGoalProgressRequestDTO request = new UpdateGoalProgressRequestDTO();
        request.setCurrentAmount(BigDecimal.valueOf(3000));

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));
        when(goalRepository.save(any(Goal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GoalResponseDTO result = goalService.updateGoalProgress(goalId, request, authenticatedUser);

        assertEquals(BigDecimal.valueOf(3000), result.getCurrentAmount());
        assertEquals(BigDecimal.valueOf(30).setScale(2), result.getProgressPercentage());
    }

    @Test
    void shouldDeleteGoalSuccessfully() {
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));
        doNothing().when(goalRepository).delete(goal);

        goalService.deleteGoal(goalId, authenticatedUser);

        verify(goalRepository, times(1)).delete(goal);
    }
}
