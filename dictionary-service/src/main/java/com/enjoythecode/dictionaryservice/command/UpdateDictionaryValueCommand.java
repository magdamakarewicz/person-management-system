package com.enjoythecode.dictionaryservice.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpdateDictionaryValueCommand {

    @Positive
    private Long id;

    @NotBlank
    private String name;

}