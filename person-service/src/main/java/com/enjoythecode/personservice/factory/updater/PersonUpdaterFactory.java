package com.enjoythecode.personservice.factory.updater;

import lombok.Getter;
import org.springframework.stereotype.Service;
import com.enjoythecode.personservice.api.DictionaryServiceClient;
import com.enjoythecode.personservice.command.UpdatePersonCommand;
import com.enjoythecode.personservice.model.Person;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Getter
public class PersonUpdaterFactory {

    private final Map<String, PersonUpdater> updaters;

    private final DictionaryServiceClient dictionaryServiceClient;

    public PersonUpdaterFactory(Set<PersonUpdater> updaters, DictionaryServiceClient dictionaryServiceClient) {
        this.updaters = updaters.stream()
                .collect(Collectors.toMap(PersonUpdater::getType, Function.identity()));
        this.dictionaryServiceClient = dictionaryServiceClient;
    }

    public Person update(UpdatePersonCommand command) {
        return updaters.get(dictionaryServiceClient.getDictionaryValueByDictionaryIdAndName(
                1L, command.getType().toLowerCase()).getName())
                .updatePerson(command);
    }

}