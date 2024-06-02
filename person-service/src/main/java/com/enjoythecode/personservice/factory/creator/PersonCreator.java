package com.enjoythecode.personservice.factory.creator;

import com.enjoythecode.personservice.command.CreatePersonCommand;
import com.enjoythecode.personservice.model.Person;

public interface PersonCreator {

    String getType();

    Person createPerson(CreatePersonCommand createCommand);

}
