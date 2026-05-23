package br.edu.ifpe.pdsc.investCalc.investCalc.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.goals.CreateGoalRequestDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.goals.GoalResponseDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.goals.UpdateGoalProgressRequestDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.goals.UpdateGoalRequestDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.Goal;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.User;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.goals.GoalNotFoundException;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.goals.UnauthorizedGoalAccessException;
import br.edu.ifpe.pdsc.investCalc.investCalc.repositories.GoalRepository;

@Service
@Transactional
public class GoalService {

    private final GoalRepository goalRepository;

    public GoalService(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    public GoalResponseDTO createGoal(
            CreateGoalRequestDTO dto,
            User authenticatedUser) {

        Goal goal = new Goal();

        goal.setName(dto.getName());
        goal.setTargetAmount(dto.getTargetAmount());
        goal.setDeadline(dto.getDeadline());
        goal.setUser(authenticatedUser);

        Goal savedGoal = goalRepository.save(goal);

        return mapToResponse(savedGoal);
    }

    @Transactional(readOnly = true)
    public List<GoalResponseDTO> getUserGoals(User authenticatedUser) {

        return goalRepository
                .findByUser(authenticatedUser)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public GoalResponseDTO getGoalById(
            Long goalId,
            User authenticatedUser) {

        Goal goal = findGoalByIdAndUser(goalId, authenticatedUser);

        return mapToResponse(goal);
    }

    public GoalResponseDTO updateGoal(
            Long goalId,
            UpdateGoalRequestDTO dto,
            User authenticatedUser) {

        Goal goal = findGoalByIdAndUser(goalId, authenticatedUser);

        goal.setName(dto.getName());
        goal.setTargetAmount(dto.getTargetAmount());
        goal.setDeadline(dto.getDeadline());

        goal.updateStatus();

        Goal updatedGoal = goalRepository.save(goal);

        return mapToResponse(updatedGoal);
    }

    public GoalResponseDTO updateGoalProgress(
            Long goalId,
            UpdateGoalProgressRequestDTO dto,
            User authenticatedUser) {

        Goal goal = findGoalByIdAndUser(goalId, authenticatedUser);

        goal.setCurrentAmount(dto.getCurrentAmount());

        Goal updatedGoal = goalRepository.save(goal);

        return mapToResponse(updatedGoal);
    }

    public void deleteGoal(
            Long goalId,
            User authenticatedUser) {

        Goal goal = findGoalByIdAndUser(goalId, authenticatedUser);

        goalRepository.delete(goal);
    }

    private Goal findGoalByIdAndUser(
            Long goalId,
            User authenticatedUser) {

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new GoalNotFoundException());

        if (!goal.getUser().getId().equals(authenticatedUser.getId())) {
            throw new UnauthorizedGoalAccessException();
        }

        return goal;
    }

    private GoalResponseDTO mapToResponse(Goal goal) {

        GoalResponseDTO dto = new GoalResponseDTO();

        dto.setId(goal.getId());
        dto.setName(goal.getName());
        dto.setTargetAmount(goal.getTargetAmount());
        dto.setCurrentAmount(goal.getCurrentAmount());
        dto.setProgressPercentage(
                goal.calculateProgressPercentage());
        dto.setDeadline(goal.getDeadline());
        dto.setStatus(goal.getStatus());
        dto.setCreatedAt(goal.getCreatedAt());

        return dto;
    }
}
