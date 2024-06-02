package com.enjoythecode.personservice.model;

import jakarta.persistence.Entity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
@Entity
public class Retiree extends Person {

    private Double pension;

    private Integer yearsOfWork;

    public Retiree(Long typeId, String firstName, String lastName, String pesel, Integer height,
                   Integer weight, String email, Double pension, Integer yearsOfWork) {
        super(typeId, firstName, lastName, pesel, height, weight, email);
        this.pension = pension;
        this.yearsOfWork = yearsOfWork;
    }

}
