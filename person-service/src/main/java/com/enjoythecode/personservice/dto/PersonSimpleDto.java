package com.enjoythecode.personservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PersonSimpleDto {

    private String firstName;

    private String lastName;

}
