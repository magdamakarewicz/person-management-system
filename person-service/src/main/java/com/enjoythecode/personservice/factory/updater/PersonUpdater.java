package com.enjoythecode.personservice.factory.updater;

import com.enjoythecode.personservice.command.UpdatePersonCommand;
import com.enjoythecode.personservice.model.Person;

public interface PersonUpdater {

    String getType();

    Person updatePerson(UpdatePersonCommand updateCommand);

}
