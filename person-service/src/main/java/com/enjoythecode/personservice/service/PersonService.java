package com.enjoythecode.personservice.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.enjoythecode.personservice.api.DictionaryServiceClient;
import com.enjoythecode.personservice.command.CreatePersonCommand;
import com.enjoythecode.personservice.command.UpdatePersonCommand;
import com.enjoythecode.personservice.exception.InvalidEntityException;
import com.enjoythecode.personservice.exception.InvalidIdException;
import com.enjoythecode.personservice.exception.PersonNotFoundException;
import com.enjoythecode.personservice.exception.UpdateOptimisticLockingException;
import com.enjoythecode.personservice.factory.creator.PersonFactory;
import com.enjoythecode.personservice.factory.specification.PersonSearchSpecification;
import com.enjoythecode.personservice.factory.updater.PersonUpdaterFactory;
import com.enjoythecode.personservice.model.Person;
import com.enjoythecode.personservice.repository.PersonRepository;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    private final PersonUpdaterFactory personUpdaterFactory;

    private final PersonSearchSpecification personSearchSpecification;

    private final PersonFactory personFactory;

    private final DictionaryServiceClient dictionaryServiceClient;

    @Transactional(readOnly = true)
    public Page<Person> getPeople(Map<String, String> params, Pageable pageable) {
        Specification<Person> specification = personSearchSpecification.filterByCriteria(params);
        return personRepository.findAll(specification, pageable);
    }

    public Person edit(UpdatePersonCommand command) {
        Person personForUpdate = personRepository.findById(
                Optional.ofNullable(command.getId())
                        .orElseThrow(() -> new InvalidIdException("Wrong id!")))
                .orElseThrow(() -> new EntityNotFoundException("Person with id " + command.getId() + " not found!"));
        try {
            Person personForSave = personUpdaterFactory.update(command);
            return personRepository.saveAndFlush(personForSave);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new UpdateOptimisticLockingException("Row was updated or deleted by another transaction " +
                    "(or unsaved-value mapping was incorrect). Current version of entity: " + personForUpdate.getVersion());
        }
    }

    public Person add(CreatePersonCommand createPersonCommand) {
        Person personForSave = personFactory.create(createPersonCommand);
        return personRepository.save(
                Optional.ofNullable(personForSave)
                        .filter(x -> Objects.isNull(x.getId()))
                        .orElseThrow(() -> new InvalidEntityException("Wrong entity for persist."))
        );
    }

    public Person getById(Long id) {
        return personRepository.findById(
                Optional.ofNullable(id)
                        .orElseThrow(() -> new InvalidIdException("Wrong id."))
        ).orElseThrow(() -> new PersonNotFoundException("Person with id " + id + " not found."));
    }

    public void deleteById(Long id) {
        Person personToDelete = getById(id);
        personRepository.delete(personToDelete);
    }

    public void createNewType(String name) {
        dictionaryServiceClient.addValueToTypeDictionary(name);
    }

}
