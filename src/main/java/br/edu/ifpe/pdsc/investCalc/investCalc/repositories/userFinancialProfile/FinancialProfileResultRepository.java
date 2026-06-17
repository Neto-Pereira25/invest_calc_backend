package br.edu.ifpe.pdsc.investCalc.investCalc.repositories.userFinancialProfile;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpe.pdsc.investCalc.investCalc.entities.User;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.userFinancialProfile.FinancialProfileResult;

public interface FinancialProfileResultRepository extends JpaRepository<FinancialProfileResult, Long> {

    List<FinancialProfileResult> findByUserOrderByAssessedAtDesc(User user);

    Optional<FinancialProfileResult> findTopByUserOrderByAssessedAtDesc(User user);

}
