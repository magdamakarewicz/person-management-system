package com.enjoythecode.dictionaryservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DictionaryDto {

    private Long id;

    private String name;

    private String[] dictionaryValues;

}
