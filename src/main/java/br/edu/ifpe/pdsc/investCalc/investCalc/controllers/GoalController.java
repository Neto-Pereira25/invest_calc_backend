package br.edu.ifpe.pdsc.investCalc.investCalc.controllers;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.ApiResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.goals.CreateGoalRequestDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.goals.GoalResponseDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.goals.UpdateGoalProgressRequestDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.goals.UpdateGoalRequestDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.User;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.GoalService;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/goals")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class GoalController {

    private final GoalService goalService;
    private final UserService userService;

    @PostMapping
    public ApiResponse<GoalResponseDTO> createGoal(
            @RequestBody @Valid CreateGoalRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User authenticatedUser = userService.getAuthenticatedUser(userDetails);

        GoalResponseDTO response = goalService.createGoal(request, authenticatedUser);

        return new ApiResponse<>(
                response,
                "Goal created successfully.");
    }

    @GetMapping
    public ApiResponse<List<GoalResponseDTO>> getUserGoals(
            @AuthenticationPrincipal UserDetails userDetails) {

        User authenticatedUser = userService.getAuthenticatedUser(userDetails);

        List<GoalResponseDTO> response = goalService.getUserGoals(authenticatedUser);

        return new ApiResponse<>(
                response,
                "Goals retrieved successfully.");
    }

    @GetMapping("/{goalId}")
    public ApiResponse<GoalResponseDTO> getGoalById(
            @PathVariable Long goalId,
            @AuthenticationPrincipal UserDetails userDetails) {

        User authenticatedUser = userService.getAuthenticatedUser(userDetails);

        GoalResponseDTO response = goalService.getGoalById(goalId, authenticatedUser);

        return new ApiResponse<>(
                response,
                "Goal retrieved successfully.");
    }

    @PutMapping("/{goalId}")
    public ApiResponse<GoalResponseDTO> updateGoal(
            @PathVariable Long goalId,
            @RequestBody @Valid UpdateGoalRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User authenticatedUser = userService.getAuthenticatedUser(userDetails);

        GoalResponseDTO response = goalService.updateGoal(
                goalId,
                request,
                authenticatedUser);

        return new ApiResponse<>(
                response,
                "Goal updated successfully.");
    }

    @PatchMapping("/{goalId}/progress")
    public ApiResponse<GoalResponseDTO> updateGoalProgress(
            @PathVariable Long goalId,
            @RequestBody @Valid UpdateGoalProgressRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User authenticatedUser = userService.getAuthenticatedUser(userDetails);

        GoalResponseDTO response = goalService.updateGoalProgress(
                goalId,
                request,
                authenticatedUser);

        return new ApiResponse<>(
                response,
                "Goal progress updated successfully.");
    }

    @DeleteMapping("/{goalId}")
    public ApiResponse<Void> deleteGoal(
            @PathVariable Long goalId,
            @AuthenticationPrincipal UserDetails userDetails) {

        User authenticatedUser = userService.getAuthenticatedUser(userDetails);

        goalService.deleteGoal(goalId, authenticatedUser);

        return new ApiResponse<>(
                null,
                "Goal deleted successfully.");
    }
}
