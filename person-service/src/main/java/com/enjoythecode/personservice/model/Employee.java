package com.enjoythecode.personservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
@Entity
public class Employee extends Person {

    private LocalDate employmentStartDate;

    /**
     * ID of the person's type in the 'position' dictionary ('position' dictionary ID in dictionarydb is 2).
     */
    @Column(name = "current_position_id")
    private Long currentPositionId;

    private Double currentSalary;

    public Employee(Long typeId, String firstName, String lastName, String pesel, Integer height,
                    Integer weight, String email, LocalDate employmentStartDate, Long currentPositionId,
                    Double currentSalary) {
        super(typeId, firstName, lastName, pesel, height, weight, email);
        this.employmentStartDate = employmentStartDate;
        this.currentPositionId = currentPositionId;
        this.currentSalary = currentSalary;
    }

}
