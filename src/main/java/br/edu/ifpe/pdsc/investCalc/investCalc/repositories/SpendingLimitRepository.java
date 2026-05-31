package br.edu.ifpe.pdsc.investCalc.investCalc.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpe.pdsc.investCalc.investCalc.entities.SpendingLimit;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.User;

public interface SpendingLimitRepository extends JpaRepository<SpendingLimit, Long> {

    Optional<SpendingLimit> findByUser(User user);

}
