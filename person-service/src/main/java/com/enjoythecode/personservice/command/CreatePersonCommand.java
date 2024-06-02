package com.enjoythecode.personservice.command;

import com.enjoythecode.personservice.validator.LettersOnly;
import com.enjoythecode.personservice.validator.Pesel;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonTypeInfo(
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        use = JsonTypeInfo.Id.NAME,
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreateStudentCommand.class, name = "student"),
        @JsonSubTypes.Type(value = CreateEmployeeCommand.class, name = "employee"),
        @JsonSubTypes.Type(value = CreateRetireeCommand.class, name = "retiree")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePersonCommand {

    @LettersOnly
    private String type;

    @LettersOnly
    private String firstName;

    @LettersOnly
    private String lastName;

    @Pesel
    private String pesel;

    @Positive(message = "Cannot be null; must be positive")
    private Integer height;

    @Positive(message = "Cannot be null; must be positive")
    private Integer weight;

    @Email
    private String email;

}
