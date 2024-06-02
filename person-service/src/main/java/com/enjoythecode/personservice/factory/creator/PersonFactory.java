package com.enjoythecode.personservice.factory.creator;

import lombok.Getter;
import org.springframework.stereotype.Service;
import com.enjoythecode.personservice.api.DictionaryServiceClient;
import com.enjoythecode.personservice.command.CreatePersonCommand;
import com.enjoythecode.personservice.model.Person;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Getter
public class PersonFactory {

    private final Map<String, PersonCreator> creators;

    private final DictionaryServiceClient dictionaryServiceClient;

    public PersonFactory(Set<PersonCreator> creators, DictionaryServiceClient dictionaryServiceClient) {
        this.creators = creators.stream()
                .collect(Collectors.toMap(PersonCreator::getType, Function.identity()));
        this.dictionaryServiceClient = dictionaryServiceClient;
    }

    public Person create(CreatePersonCommand command) {
        return creators.get(dictionaryServiceClient.getDictionaryValueByDictionaryIdAndName(
                1L, command.getType().toLowerCase()).getName())
                .createPerson(command);
    }

}
