package com.enjoythecode.personservice.factory.cteatorfromcsv;

import lombok.Getter;
import org.springframework.stereotype.Service;
import com.enjoythecode.personservice.command.CreatePersonFromCsvCommand;
import com.enjoythecode.personservice.model.Person;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Getter
public class PersonFromCsvFactory {

    private final Map<String, PersonFromCsvCreator> creators;

    public PersonFromCsvFactory(Set<PersonFromCsvCreator> creators) {
        this.creators = creators.stream()
                .collect(Collectors.toMap(PersonFromCsvCreator::getType, Function.identity()));
    }

    public Person create(CreatePersonFromCsvCommand command) {
        return creators.get(command.getPersonType().toLowerCase())
                .createPerson(command.getParameters());
    }

}
