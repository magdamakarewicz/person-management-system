package com.enjoythecode.personservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class EmployeePosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    /**
     * ID of the employee's position in the 'position' dictionary ('position' dictionary ID in dictionarydb is 2).
     */
    @Column(name = "position_id")
    private Long positionId;

    private LocalDate startDate;

    private LocalDate endDate;

    private Double salary;

    public EmployeePosition(Employee employee, Long positionId, LocalDate startDate,
                            LocalDate endDate, Double salary) {
        this.employee = employee;
        this.positionId = positionId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.salary = salary;
        this.setEmployee(employee);
        this.setPositionId(positionId);
    }

}
