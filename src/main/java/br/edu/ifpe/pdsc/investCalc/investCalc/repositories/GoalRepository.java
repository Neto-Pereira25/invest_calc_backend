package br.edu.ifpe.pdsc.investCalc.investCalc.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpe.pdsc.investCalc.investCalc.entities.Goal;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.User;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.GoalStatus;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByUser(User user);

    List<Goal> findByUserAndStatus(User user, GoalStatus status);
}
