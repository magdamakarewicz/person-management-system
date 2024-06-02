package com.enjoythecode.personservice.command;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class CreateEmployeeCommand extends CreatePersonCommand {

    @NotNull(message = "Cannot be null")
    private LocalDate employmentStartDate;

    @Positive
    private Long currentPositionId;

    @PositiveOrZero(message = "Cannot be null; must be positive")
    private Double currentSalary;

    public CreateEmployeeCommand(String type, String firstName, String lastName, String pesel,
                                 @Positive(message = "Cannot be null; must be positive") Integer height,
                                 @Positive(message = "Cannot be null; must be positive") Integer weight,
                                 @Email String email, @NotNull(message = "Cannot be null") LocalDate employmentStartDate,
                                 @Positive Long currentPositionId,
                                 @PositiveOrZero(message = "Cannot be null; must be positive") Double currentSalary) {
        super(type, firstName, lastName, pesel, height, weight, email);
        this.employmentStartDate = employmentStartDate;
        this.currentPositionId = currentPositionId;
        this.currentSalary = currentSalary;
    }

}
