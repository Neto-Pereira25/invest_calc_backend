package br.edu.ifpe.pdsc.investCalc.investCalc.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import br.edu.ifpe.pdsc.investCalc.investCalc.entities.User;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.Role;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserResponse(Long id, String name, String email, Role role, String password) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), null);
    }
}
