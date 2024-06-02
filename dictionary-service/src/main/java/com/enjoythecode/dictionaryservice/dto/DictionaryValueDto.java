package com.enjoythecode.dictionaryservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DictionaryValueDto {

    private Long id;

    private String name;

    private DictionarySimpleDto dictionary;

}
