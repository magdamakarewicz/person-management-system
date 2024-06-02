package com.enjoythecode.personservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "people")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
public class Person implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID of the person's type in the 'type' dictionary ('type' dictionary ID in dictionarydb is 1).
     */
    @Column(name = "type_id")
    private Long typeId;

    private String firstName;

    private String lastName;

    private String pesel;

    private Integer height;

    private Integer weight;

    private String email;

    @Version
    private Long version;

    public Person(Long typeId, String firstName, String lastName, String pesel, Integer height,
                  Integer weight, String email) {
        this.typeId = typeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.pesel = pesel;
        this.height = height;
        this.weight = weight;
        this.email = email;
    }

}
