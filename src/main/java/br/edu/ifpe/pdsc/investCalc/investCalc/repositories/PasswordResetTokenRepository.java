package br.edu.ifpe.pdsc.investCalc.investCalc.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpe.pdsc.investCalc.investCalc.entities.PasswordResetToken;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.User;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    @Transactional
    void deleteByUser(User user);

    @Query("SELECT prt FROM PasswordResetToken prt WHERE prt.user.email = :email AND prt.used = false ORDER BY prt.id DESC LIMIT 1")
    Optional<PasswordResetToken> findLatestUnusedByEmail(@Param("email") String email);
}