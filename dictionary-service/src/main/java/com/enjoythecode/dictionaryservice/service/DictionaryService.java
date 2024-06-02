package com.enjoythecode.dictionaryservice.service;

import com.enjoythecode.dictionaryservice.command.CreateDictionaryCommand;
import com.enjoythecode.dictionaryservice.command.UpdateDictionaryCommand;
import com.enjoythecode.dictionaryservice.exception.*;
import com.enjoythecode.dictionaryservice.model.Dictionary;
import com.enjoythecode.dictionaryservice.model.DictionaryValue;
import com.enjoythecode.dictionaryservice.repository.DictionaryRepository;
import com.enjoythecode.dictionaryservice.repository.DictionaryValueRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DictionaryService {

    private final DictionaryRepository dictionaryRepository;

    private final DictionaryValueRepository dictionaryValueRepository;

    private final DictionaryValueService dictionaryValueService;

    @Transactional(readOnly = true)
    @Cacheable("dictionaries")
    public Dictionary getDictionaryById(Long id) {
        return dictionaryRepository.findByIdWithDictionaryValues(
                Optional.ofNullable(id)
                        .orElseThrow(() -> new InvalidIdException("Id cannot be null."))
        ).orElseThrow(() -> new DictionaryNotFoundException("Dictionary with id " + id + " not found."));
    }

    @Transactional(readOnly = true)
    public List<Dictionary> getAllDictionaries() {
        return dictionaryRepository.findAll();
    }

    @Transactional
    public Dictionary addDictionary(CreateDictionaryCommand command) {
        Dictionary dictionaryForSave = new Dictionary();
        dictionaryForSave.setName(command.getName());
        return dictionaryRepository.save(dictionaryForSave);
    }

    @Transactional
    public Dictionary updateDictionaryName(Long dictionaryId, UpdateDictionaryCommand command) {
        if (!dictionaryId.equals(command.getId()))
            throw new InvalidIdException("Identifiers provided in path variable and request body do not match");
        Dictionary dictionary = getDictionaryById(command.getId());
        dictionary.setName(command.getName());
        return dictionaryRepository.save(dictionary);
    }

    public void deleteDictionaryById(Long id) {
        Dictionary dictionaryToDelete = getDictionaryById(id);
        if (!dictionaryToDelete.getDictionaryValues().isEmpty())
            throw new IllegalDictionaryStateException("A dictionary with values cannot be deleted. Delete dictionary " +
                    "values first.");
        dictionaryRepository.delete(dictionaryToDelete);
    }

    @Transactional
    public void deleteValuesFromDictionary(Long id) {
        Dictionary dictionaryToEmpty = getDictionaryById(id);
        if (dictionaryToEmpty.getDictionaryValues().isEmpty())
            throw new IllegalDictionaryStateException("Dictionary is already empty.");
        dictionaryToEmpty.getDictionaryValues().forEach(value -> value.setDictionary(null));
        dictionaryValueRepository.saveAll(dictionaryToEmpty.getDictionaryValues());
        dictionaryToEmpty.getDictionaryValues().clear();
        dictionaryRepository.save(dictionaryToEmpty);
    }

    @Transactional
    public Dictionary addValueToDictionary(Long dictionaryId, Long dictionaryValueId) {
        Dictionary dictionary = getDictionaryById(dictionaryId);
        DictionaryValue dictionaryValue = dictionaryValueService.getDictionaryValueById(dictionaryValueId);
        if (dictionaryValue.getDictionary() != null)
            throw new IllegalDictionaryValueStateException("Value '" + dictionaryValue.getName() + "' already exists " +
                    "in '" + dictionaryValue.getDictionary().getName() + "' dictionary.");
        boolean nameExistsInDictionary = dictionary.getDictionaryValues().stream()
                .anyMatch(existingValue -> existingValue.getName().equals(dictionaryValue.getName()));
        if (nameExistsInDictionary)
            throw new IllegalDictionaryValueStateException("Value with the same name already exists in the dictionary.");
        dictionary.getDictionaryValues().add(dictionaryValue);
        dictionaryValue.setDictionary(dictionary);
        return dictionaryRepository.save(dictionary);
    }

    @Transactional
    public Dictionary removeValueFromDictionary(Long dictionaryId, Long dictionaryValueId) {
        Dictionary dictionary = getDictionaryById(dictionaryId);
        DictionaryValue dictionaryValue = dictionaryValueService.getDictionaryValueById(dictionaryValueId);
        if (!dictionary.getDictionaryValues().contains(dictionaryValue))
            throw new DictionaryValueNotFoundException(
                    "Dictionary does not contain the '" + dictionaryValue.getName() + "' value.");
        dictionary.getDictionaryValues().remove(dictionaryValue);
        dictionaryValue.setDictionary(null);
        return dictionaryRepository.save(dictionary);
    }

    @Transactional(readOnly = true)
    public List<DictionaryValue> getValuesByDictionaryId(Long id) {
        if (!dictionaryRepository.existsById(id))
            throw new DictionaryNotFoundException("Dictionary with id '" + id + "' not found.");
        return dictionaryValueRepository.findValuesByDictionaryId(id);
    }

    @Transactional
    public Dictionary addValueByNameToTypeDictionary(String name) {
        Long dictionaryId = 1L;
        DictionaryValue dictionaryValue = new DictionaryValue(name);
        Dictionary typeDictionary = getDictionaryById(dictionaryId);
        boolean nameExistsInDictionary = typeDictionary.getDictionaryValues().stream()
                .anyMatch(existingValue -> existingValue.getName().equals(dictionaryValue.getName()));
        if (nameExistsInDictionary)
            throw new IllegalDictionaryValueStateException("Value with the same name already exists in the dictionary.");
        typeDictionary.getDictionaryValues().add(dictionaryValue);
        dictionaryValue.setDictionary(typeDictionary);
        return dictionaryRepository.save(typeDictionary);
    }

}
