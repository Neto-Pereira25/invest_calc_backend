package br.edu.ifpe.pdsc.investCalc.investCalc.services;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.monthlySpendingLimit.SpendingLimitResponseDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.monthlySpendingLimit.UpdateSpendingLimitRequestDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.SpendingLimit;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.User;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.monthlySpendingLimit.SpendingLimitAlreadyExistsException;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.monthlySpendingLimit.SpendingLimitNotConfiguredException;
import br.edu.ifpe.pdsc.investCalc.investCalc.repositories.SpendingLimitRepository;

@Service
@Transactional
public class SpendingLimitService {

    private final SpendingLimitRepository repository;

    public SpendingLimitService(SpendingLimitRepository repository) {
        this.repository = repository;
    }

    public SpendingLimitResponseDTO createLimit(
            UpdateSpendingLimitRequestDTO dto,
            User authenticatedUser) {

        if (repository.findByUser(authenticatedUser).isPresent()) {
            throw new SpendingLimitAlreadyExistsException();
        }

        SpendingLimit spendingLimit = new SpendingLimit();

        spendingLimit.setAmount(dto.amount());
        spendingLimit.setUser(authenticatedUser);

        SpendingLimit savedLimit = repository.save(spendingLimit);

        return mapToResponse(savedLimit);
    }

    @Transactional(readOnly = true)
    public SpendingLimitResponseDTO getLimit(
            User authenticatedUser) {

        Optional<SpendingLimit> spendingLimit = repository.findByUser(authenticatedUser);

        return spendingLimit
                .map(this::mapToResponse)
                .orElse(null);
    }

    public SpendingLimitResponseDTO updateLimit(
            UpdateSpendingLimitRequestDTO dto,
            User authenticatedUser) {

        SpendingLimit spendingLimit = repository.findByUser(authenticatedUser)
                .orElseThrow(
                        SpendingLimitNotConfiguredException::new);

        spendingLimit.setAmount(dto.amount());

        SpendingLimit updatedLimit = repository.save(spendingLimit);

        return mapToResponse(updatedLimit);
    }

    public void deleteLimit(
            User authenticatedUser) {

        SpendingLimit spendingLimit = repository.findByUser(authenticatedUser)
                .orElseThrow(
                        SpendingLimitNotConfiguredException::new);

        repository.delete(spendingLimit);
    }

    private SpendingLimitResponseDTO mapToResponse(
            SpendingLimit spendingLimit) {

        return new SpendingLimitResponseDTO(
                spendingLimit.getId(),
                spendingLimit.getAmount(),
                spendingLimit.getCreatedAt(),
                spendingLimit.getUpdatedAt());
    }
}
