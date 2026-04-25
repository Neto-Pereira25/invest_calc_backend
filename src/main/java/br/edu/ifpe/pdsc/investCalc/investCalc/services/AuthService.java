package br.edu.ifpe.pdsc.investCalc.investCalc.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.AuthResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.LoginRequest;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.RegisterRequest;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.User;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.EmailAlreadyExistsException;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.InvalidPasswordException;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.UserNotFoundException;
import br.edu.ifpe.pdsc.investCalc.investCalc.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(RegisterRequest request) {

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new EmailAlreadyExistsException();
        }

        String encryptedPassword = passwordEncoder.encode(request.password());

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(encryptedPassword);

        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidPasswordException();
        }

        return new AuthResponse("TOKEN_FAKE");
    }
}
