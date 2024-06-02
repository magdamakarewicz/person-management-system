package com.enjoythecode.personservice.factory.converter;

import com.enjoythecode.personservice.dto.PersonDto;
import com.enjoythecode.personservice.model.Person;

public interface PersonDtoConverter {

    String getType();

    PersonDto convert(Person person);

}
