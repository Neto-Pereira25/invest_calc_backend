package br.edu.ifpe.pdsc.investCalc.investCalc.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.monthlySpendingLimit.SpendingLimitResponseDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.monthlySpendingLimit.UpdateSpendingLimitRequestDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.SpendingLimit;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.User;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.monthlySpendingLimit.SpendingLimitAlreadyExistsException;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.monthlySpendingLimit.SpendingLimitNotConfiguredException;
import br.edu.ifpe.pdsc.investCalc.investCalc.repositories.SpendingLimitRepository;

@ExtendWith(MockitoExtension.class)
class SpendingLimitServiceTest {

    @Mock
    private SpendingLimitRepository repository;

    @InjectMocks
    private SpendingLimitService service;

    private User authenticatedUser;
    private SpendingLimit spendingLimit;

    @BeforeEach
    void setup() {
        authenticatedUser = new User();
        authenticatedUser.setId(1L);
        authenticatedUser.setEmail("user@email.com");

        spendingLimit = new SpendingLimit();
        spendingLimit.setId(10L);
        spendingLimit.setAmount(BigDecimal.valueOf(1500));
        spendingLimit.setUser(authenticatedUser);
        spendingLimit.setCreatedAt(LocalDateTime.now().minusDays(1));
        spendingLimit.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should create spending limit successfully")
    void shouldCreateSpendingLimitSuccessfully() {
        UpdateSpendingLimitRequestDTO request = new UpdateSpendingLimitRequestDTO(BigDecimal.valueOf(2200));

        when(repository.findByUser(authenticatedUser)).thenReturn(Optional.empty());
        when(repository.save(any(SpendingLimit.class))).thenAnswer(invocation -> {
            SpendingLimit saved = invocation.getArgument(0);
            saved.setId(20L);
            saved.setCreatedAt(LocalDateTime.now().minusHours(1));
            saved.setUpdatedAt(LocalDateTime.now());
            return saved;
        });

        SpendingLimitResponseDTO response = service.createLimit(request, authenticatedUser);

        assertNotNull(response);
        assertEquals(20L, response.id());
        assertEquals(BigDecimal.valueOf(2200), response.amount());
        verify(repository, times(1)).save(any(SpendingLimit.class));
    }

    @Test
    @DisplayName("Should throw exception when creating limit that already exists")
    void shouldThrowExceptionWhenCreatingLimitThatAlreadyExists() {
        UpdateSpendingLimitRequestDTO request = new UpdateSpendingLimitRequestDTO(BigDecimal.valueOf(2200));
        when(repository.findByUser(authenticatedUser)).thenReturn(Optional.of(spendingLimit));

        SpendingLimitAlreadyExistsException exception = assertThrows(
                SpendingLimitAlreadyExistsException.class,
                () -> service.createLimit(request, authenticatedUser));

        assertEquals("Limite mensal ja configurado para este usuario.", exception.getMessage());
        verify(repository, never()).save(any(SpendingLimit.class));
    }

    @Test
    @DisplayName("Should return configured spending limit")
    void shouldReturnConfiguredSpendingLimit() {
        when(repository.findByUser(authenticatedUser)).thenReturn(Optional.of(spendingLimit));

        SpendingLimitResponseDTO response = service.getLimit(authenticatedUser);

        assertNotNull(response);
        assertEquals(10L, response.id());
        assertEquals(BigDecimal.valueOf(1500), response.amount());
    }

    @Test
    @DisplayName("Should return null when spending limit is not configured")
    void shouldReturnNullWhenSpendingLimitIsNotConfigured() {
        when(repository.findByUser(authenticatedUser)).thenReturn(Optional.empty());

        SpendingLimitResponseDTO response = service.getLimit(authenticatedUser);

        assertNull(response);
    }

    @Test
    @DisplayName("Should update spending limit successfully")
    void shouldUpdateSpendingLimitSuccessfully() {
        UpdateSpendingLimitRequestDTO request = new UpdateSpendingLimitRequestDTO(BigDecimal.valueOf(3000));

        when(repository.findByUser(authenticatedUser)).thenReturn(Optional.of(spendingLimit));
        when(repository.save(any(SpendingLimit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SpendingLimitResponseDTO response = service.updateLimit(request, authenticatedUser);

        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(3000), response.amount());
        verify(repository, times(1)).save(any(SpendingLimit.class));
    }

    @Test
    @DisplayName("Should throw exception when updating limit that is not configured")
    void shouldThrowExceptionWhenUpdatingLimitThatIsNotConfigured() {
        UpdateSpendingLimitRequestDTO request = new UpdateSpendingLimitRequestDTO(BigDecimal.valueOf(3000));
        when(repository.findByUser(authenticatedUser)).thenReturn(Optional.empty());

        SpendingLimitNotConfiguredException exception = assertThrows(
                SpendingLimitNotConfiguredException.class,
                () -> service.updateLimit(request, authenticatedUser));

        assertEquals("Nenhum limite mensal configurado.", exception.getMessage());
        verify(repository, never()).save(any(SpendingLimit.class));
    }

    @Test
    @DisplayName("Should delete spending limit successfully")
    void shouldDeleteSpendingLimitSuccessfully() {
        when(repository.findByUser(authenticatedUser)).thenReturn(Optional.of(spendingLimit));

        service.deleteLimit(authenticatedUser);

        verify(repository, times(1)).delete(spendingLimit);
    }

    @Test
    @DisplayName("Should throw exception when deleting limit that is not configured")
    void shouldThrowExceptionWhenDeletingLimitThatIsNotConfigured() {
        when(repository.findByUser(authenticatedUser)).thenReturn(Optional.empty());

        SpendingLimitNotConfiguredException exception = assertThrows(
                SpendingLimitNotConfiguredException.class,
                () -> service.deleteLimit(authenticatedUser));

        assertEquals("Nenhum limite mensal configurado.", exception.getMessage());
        verify(repository, never()).delete(any(SpendingLimit.class));
    }
}