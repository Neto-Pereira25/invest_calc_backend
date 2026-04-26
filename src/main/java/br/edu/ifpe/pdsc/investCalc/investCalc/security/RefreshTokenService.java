package br.edu.ifpe.pdsc.investCalc.investCalc.security;

import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Service;

import br.edu.ifpe.pdsc.investCalc.investCalc.entities.RefreshToken;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.User;
import br.edu.ifpe.pdsc.investCalc.investCalc.repositories.RefreshTokenRepository;
import br.edu.ifpe.pdsc.investCalc.investCalc.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final UserRepository userRepository;

    public RefreshToken createRefreshToken(String email) {

        User user = userRepository.findByEmail(email).orElseThrow();

        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)); // 7 dias

        return repository.save(token);
    }

    public RefreshToken validate(String token) {

        RefreshToken refreshToken = repository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token inválido"));

        if (refreshToken.getExpiration().before(new Date())) {
            repository.delete(refreshToken);
            throw new RuntimeException("Refresh token expirado");
        }

        return refreshToken;
    }
}
