package com.enjoythecode.personservice.command;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class CreateEmployeePositionCommand {

    @Positive
    private Long positionId;

    @NotNull(message = "Cannot be null")
    private LocalDate startDate;

    @PositiveOrZero(message = "Cannot be null; must be positive")
    private Double salary;

}
