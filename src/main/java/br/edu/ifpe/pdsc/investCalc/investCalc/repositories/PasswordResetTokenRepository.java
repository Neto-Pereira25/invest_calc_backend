package br.edu.ifpe.pdsc.investCalc.investCalc.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpe.pdsc.investCalc.investCalc.entities.PasswordResetToken;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.User;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    @Transactional
    void deleteByUser(User user);
}