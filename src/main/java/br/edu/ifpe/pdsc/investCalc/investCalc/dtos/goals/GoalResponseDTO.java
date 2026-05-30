package br.edu.ifpe.pdsc.investCalc.investCalc.dtos.goals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import br.edu.ifpe.pdsc.investCalc.investCalc.enums.GoalStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GoalResponseDTO {

    private Long id;

    private String name;

    private BigDecimal targetAmount;

    private BigDecimal currentAmount;

    private BigDecimal progressPercentage;

    private LocalDate deadline;

    private GoalStatus status;

    private LocalDateTime createdAt;
}
