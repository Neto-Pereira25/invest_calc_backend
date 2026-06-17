package br.edu.ifpe.pdsc.investCalc.investCalc.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import br.edu.ifpe.pdsc.investCalc.investCalc.entities.User;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.UserNotFoundException;
import br.edu.ifpe.pdsc.investCalc.investCalc.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getAuthenticatedUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(UserNotFoundException::new);
    }

    public User updateAuthenticatedUserName(UserDetails userDetails, String name) {
        var user = getAuthenticatedUser(userDetails);
        user.setName(name);
        return userRepository.save(user);
    }
}
