package br.edu.ifpe.pdsc.investCalc.investCalc.exceptions;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.ApiResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.goals.GoalNotFoundException;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.goals.UnauthorizedGoalAccessException;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.monthlySpendingLimit.SpendingLimitAlreadyExistsException;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.monthlySpendingLimit.SpendingLimitNotConfiguredException;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.transaction.SubcategoryNotFoundException;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.transaction.TransactionNotFoundException;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.transaction.UnauthorizedTransactionAccessException;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.userFinancialProfile.FinancialProfileNotFoundException;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.userFinancialProfile.InvalidFinancialProfileAssessmentException;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(EmailAlreadyExistsException.class)
        public ResponseEntity<ApiResponse<Object>> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(new ApiResponse<>(null, ex.getMessage()));
        }

        @ExceptionHandler(UserNotFoundException.class)
        public ResponseEntity<ApiResponse<Object>> handleUserNotFound(UserNotFoundException ex) {
                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(new ApiResponse<>(null, ex.getMessage()));
        }

        @ExceptionHandler(InvalidPasswordException.class)
        public ResponseEntity<ApiResponse<Object>> handleInvalidPassword(InvalidPasswordException ex) {
                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(new ApiResponse<>(null, ex.getMessage()));
        }

        @ExceptionHandler(InvalidPasswordResetTokenException.class)
        public ResponseEntity<ApiResponse<Object>> handleInvalidPasswordResetToken(
                        InvalidPasswordResetTokenException ex) {
                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(new ApiResponse<>(null, ex.getMessage()));
        }

        @ExceptionHandler(PasswordResetTokenExpiredException.class)
        public ResponseEntity<ApiResponse<Object>> handlePasswordResetTokenExpired(
                        PasswordResetTokenExpiredException ex) {
                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(new ApiResponse<>(null, ex.getMessage()));
        }

        @ExceptionHandler(PasswordResetTokenAlreadyUsedException.class)
        public ResponseEntity<ApiResponse<Object>> handlePasswordResetTokenAlreadyUsed(
                        PasswordResetTokenAlreadyUsedException ex) {
                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(new ApiResponse<>(null, ex.getMessage()));
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException ex) {

                List<String> errors = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(error -> error.getDefaultMessage())
                                .toList();

                return ResponseEntity
                                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                                .body(new ApiResponse<>(errors, "Erro de validacao"));
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
                System.out.println();
                System.out.println(ex.getMessage());
                System.out.println();
                ex.printStackTrace();
                System.out.println();
                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new ApiResponse<>(null, "Erro interno do servidor"));
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex) {

                return ResponseEntity
                                .status(HttpStatus.FORBIDDEN)
                                .body(new ApiResponse<>(null, "Acesso negado"));
        }

        @ExceptionHandler(TransactionNotFoundException.class)
        public ResponseEntity<ApiResponse<Object>> handleTransactionNotFound(TransactionNotFoundException ex) {
                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(new ApiResponse<>(null, ex.getMessage()));
        }

        @ExceptionHandler(UnauthorizedTransactionAccessException.class)
        public ResponseEntity<ApiResponse<Object>> handleUnauthorizedTransactionAccess(
                        UnauthorizedTransactionAccessException ex) {
                return ResponseEntity
                                .status(HttpStatus.FORBIDDEN)
                                .body(new ApiResponse<>(null, ex.getMessage()));
        }

        @ExceptionHandler(SubcategoryNotFoundException.class)
        public ResponseEntity<ApiResponse<Object>> handleSubcategoryNotFound(SubcategoryNotFoundException ex) {
                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(new ApiResponse<>(null, ex.getMessage()));
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ApiResponse<Object>> handleJsonParseError(HttpMessageNotReadableException ex) {

                String message = "Erro ao processar a requisição: JSON inválido ou valor de campo incorreto.";

                Throwable cause = ex.getCause();

                if (cause instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException invalidFormatException) {
                        String fieldName = invalidFormatException.getPath().get(0).getFieldName();
                        Object invalidValue = invalidFormatException.getValue();
                        Class<?> targetType = invalidFormatException.getTargetType();

                        // Se for enum
                        if (targetType.isEnum()) {

                                Object[] acceptedValues = targetType.getEnumConstants();

                                message = String.format(
                                                "Valor inválido para '%s'. Valores permitidos: %s",
                                                fieldName,
                                                java.util.Arrays.toString(acceptedValues));
                        } else {
                                message = String.format(
                                                "Campo '%s' recebeu um valor inválido: %s",
                                                fieldName,
                                                invalidValue);
                        }
                }

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(new ApiResponse<>(null, message));
        }

        @ExceptionHandler(GoalNotFoundException.class)
        public ResponseEntity<ApiResponse<Object>> handleGoalNotFound(
                        GoalNotFoundException ex) {

                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(new ApiResponse<>(null, ex.getMessage()));
        }

        @ExceptionHandler(UnauthorizedGoalAccessException.class)
        public ResponseEntity<ApiResponse<Object>> handleUnauthorizedGoalAccess(
                        UnauthorizedGoalAccessException ex) {

                return ResponseEntity
                                .status(HttpStatus.FORBIDDEN)
                                .body(new ApiResponse<>(null, ex.getMessage()));
        }

        @ExceptionHandler(SpendingLimitAlreadyExistsException.class)
        public ResponseEntity<ApiResponse<Object>> handleSpendingLimitAlreadyExists(
                        SpendingLimitAlreadyExistsException ex) {

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(new ApiResponse<>(null, ex.getMessage()));
        }

        @ExceptionHandler(SpendingLimitNotConfiguredException.class)
        public ResponseEntity<ApiResponse<Object>> handleSpendingLimitNotConfigured(
                        SpendingLimitNotConfiguredException ex) {

                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(new ApiResponse<>(null, ex.getMessage()));
        }

        @ExceptionHandler(FinancialProfileNotFoundException.class)
        public ResponseEntity<ApiResponse<Object>> handleFinancialProfileNotFound(
                        FinancialProfileNotFoundException ex) {

                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(new ApiResponse<>(null, ex.getMessage()));
        }

        @ExceptionHandler(InvalidFinancialProfileAssessmentException.class)
        public ResponseEntity<ApiResponse<Object>> handleInvalidFinancialProfileAssessment(
                        InvalidFinancialProfileAssessmentException ex) {

                return ResponseEntity
                                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                                .body(new ApiResponse<>(null, ex.getMessage()));
        }
}
