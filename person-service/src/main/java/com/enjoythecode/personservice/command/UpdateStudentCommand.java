package com.enjoythecode.personservice.command;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class UpdateStudentCommand extends UpdatePersonCommand {

    @Positive
    private Long universityNameId;

    @Positive(message = "Cannot be null; must be positive")
    @Max(value = 6, message = "Should not be greater than 6")
    private Integer enrollmentYear;

    @Positive
    private Long fieldOfStudyId;

    @PositiveOrZero(message = "Cannot be null; must be positive")
    private Double scholarship;

}
