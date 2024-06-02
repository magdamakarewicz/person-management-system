package com.enjoythecode.dictionaryservice.command;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateDictionaryValueCommand {

    @NotBlank
    private String name;

}
