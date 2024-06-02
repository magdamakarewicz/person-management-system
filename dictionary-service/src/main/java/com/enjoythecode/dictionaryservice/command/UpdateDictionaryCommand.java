package com.enjoythecode.dictionaryservice.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateDictionaryCommand {

    @Positive
    private Long id;

    @NotBlank
    private String name;

}
