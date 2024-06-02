package com.enjoythecode.personservice.factory.cteatorfromcsv;

import com.enjoythecode.personservice.model.Person;

public interface PersonFromCsvCreator {

    String getType();

    Person createPerson(String[] parameters);

}
