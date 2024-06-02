package com.enjoythecode.personservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
@Entity
public class Student extends Person {

    /**
     * ID of the person's type in the 'university name' dictionary ('university name' dictionary ID in dictionarydb is 3).
     */
    @Column(name = "university_name_id")
    private Long universityNameId;

    private Integer enrollmentYear;

    /**
     * ID of the person's type in the 'field of study' dictionary ('field of study' dictionary ID in dictionarydb is 4).
     */
    @Column(name = "field_of_study_id")
    private Long fieldOfStudyId;

    private Double scholarship;

    public Student(Long typeId, String firstName, String lastName, String pesel, Integer height,
                   Integer weight, String email, Long universityNameId, Integer enrollmentYear,
                   Long fieldOfStudyId, Double scholarship) {
        super(typeId, firstName, lastName, pesel, height, weight, email);
        this.universityNameId = universityNameId;
        this.enrollmentYear = enrollmentYear;
        this.fieldOfStudyId = fieldOfStudyId;
        this.scholarship = scholarship;
    }

}
