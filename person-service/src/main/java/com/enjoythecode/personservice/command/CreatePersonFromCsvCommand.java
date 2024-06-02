package com.enjoythecode.personservice.command;

import com.enjoythecode.personservice.validator.LettersOnly;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreatePersonFromCsvCommand {

    @LettersOnly(message = "Field cannot be null; can contain only letters")
    private String personType;

    @Size(min = 7)
    private String[] parameters;

}
