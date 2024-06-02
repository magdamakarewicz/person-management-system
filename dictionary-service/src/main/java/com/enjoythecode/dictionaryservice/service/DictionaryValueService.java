package com.enjoythecode.dictionaryservice.service;

import com.enjoythecode.dictionaryservice.command.CreateDictionaryValueCommand;
import com.enjoythecode.dictionaryservice.command.UpdateDictionaryValueCommand;
import com.enjoythecode.dictionaryservice.exception.DictionaryValueNotFoundException;
import com.enjoythecode.dictionaryservice.exception.InvalidIdException;
import com.enjoythecode.dictionaryservice.model.DictionaryValue;
import com.enjoythecode.dictionaryservice.repository.DictionaryValueRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DictionaryValueService {

    private final DictionaryValueRepository dictionaryValueRepository;

    @Transactional(readOnly = true)
    @Cacheable("dictionaryValues")
    public DictionaryValue getDictionaryValueById(Long id) {
        return dictionaryValueRepository.findById(
                Optional.ofNullable(id)
                        .orElseThrow(() -> new InvalidIdException("Id cannot be null."))
        ).orElseThrow(() -> new DictionaryValueNotFoundException("Dictionary value with id " + id + " not found."));
    }

    @Transactional(readOnly = true)
    public List<DictionaryValue> getAllDictionaryValues() {
        return dictionaryValueRepository.findAll();
    }

    @Transactional(readOnly = true)
    public DictionaryValue getDictionaryValueByDictionaryIdAndName(Long dictionaryId, String name) {
        return dictionaryValueRepository.findByDictionaryIdAndName(dictionaryId, name)
                .orElseThrow(() -> new DictionaryValueNotFoundException(
                        "Dictionary value '" + name + "' not found in the dictionary with id " + dictionaryId));
    }

    @Transactional
    public DictionaryValue addDictionaryValue(CreateDictionaryValueCommand command) {
        DictionaryValue dictionaryValueForSave = new DictionaryValue();
        dictionaryValueForSave.setName(command.getName().toLowerCase());
        return dictionaryValueRepository.save(dictionaryValueForSave);
    }

    @Transactional
    public DictionaryValue updateDictionaryValueName(Long id, UpdateDictionaryValueCommand command) {
        if (!id.equals(command.getId()))
            throw new InvalidIdException("Identifiers provided in path variable and request body do not match");
        DictionaryValue dictionaryValue = getDictionaryValueById(command.getId());
        dictionaryValue.setName(command.getName().toLowerCase());
        return dictionaryValueRepository.save(dictionaryValue);
    }

    public void deleteDictionaryValueById(Long id) {
        DictionaryValue dictionaryValueToDelete = getDictionaryValueById(id);
        dictionaryValueRepository.delete(dictionaryValueToDelete);
    }

    @Transactional
    public void deleteUnassignedValues() {
        dictionaryValueRepository.deleteUnassignedValues();
    }

}
