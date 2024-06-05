package com.enjoythecode.dictionaryservice.service;

import com.enjoythecode.dictionaryservice.command.CreateDictionaryValueCommand;
import com.enjoythecode.dictionaryservice.command.UpdateDictionaryValueCommand;
import com.enjoythecode.dictionaryservice.exception.DictionaryValueNotFoundException;
import com.enjoythecode.dictionaryservice.exception.InvalidIdException;
import com.enjoythecode.dictionaryservice.model.DictionaryValue;
import com.enjoythecode.dictionaryservice.repository.DictionaryValueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DictionaryValueServiceTest {

    @Mock
    private DictionaryValueRepository dictionaryValueRepository;

    @InjectMocks
    private DictionaryValueService dictionaryValueService;

    private DictionaryValue dictionaryValue;

    private List<DictionaryValue> dictionaryValues;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        dictionaryValue = new DictionaryValue();
        dictionaryValue.setId(1L);
        dictionaryValue.setName("Test Value");

        dictionaryValues = new ArrayList<>();
        dictionaryValues.add(dictionaryValue);
    }

    @Test
    public void shouldReturnDictionaryValueById() {
        //given
        when(dictionaryValueRepository.findById(1L)).thenReturn(Optional.of(dictionaryValue));

        //when
        DictionaryValue foundDictionaryValue = dictionaryValueService.getDictionaryValueById(1L);

        //then
        assertEquals(dictionaryValue, foundDictionaryValue);
        verify(dictionaryValueRepository, atLeastOnce()).findById(1L);
    }

    @Test
    public void shouldThrowInvalidIdExceptionWhenIdIsNull() {
        //when/then
        InvalidIdException exception = assertThrows(InvalidIdException.class,
                () -> dictionaryValueService.getDictionaryValueById(null));
        assertEquals("Id cannot be null.", exception.getMessage());
    }

    @Test
    public void shouldThrowDictionaryValueNotFoundExceptionWhenDictionaryValueNotFound() {
        //given
        when(dictionaryValueRepository.findById(1L)).thenReturn(Optional.empty());

        //when/then
        DictionaryValueNotFoundException exception = assertThrows(DictionaryValueNotFoundException.class,
                () -> dictionaryValueService.getDictionaryValueById(1L));
        assertEquals("Dictionary value with id 1 not found.", exception.getMessage());
    }

    @Test
    public void shouldReturnAllDictionaryValues() {
        //given
        when(dictionaryValueRepository.findAll()).thenReturn(dictionaryValues);

        //when
        List<DictionaryValue> foundDictionaryValues = dictionaryValueService.getAllDictionaryValues();

        //then
        assertEquals(dictionaryValues, foundDictionaryValues);
        verify(dictionaryValueRepository, times(1)).findAll();
    }

    @Test
    public void shouldReturnDictionaryValueByDictionaryIdAndName() {
        //given
        when(dictionaryValueRepository.findByDictionaryIdAndName(1L, "Test Value"))
                .thenReturn(Optional.of(dictionaryValue));

        //when
        DictionaryValue foundDictionaryValue = dictionaryValueService
                .getDictionaryValueByDictionaryIdAndName(1L, "Test Value");

        //then
        assertEquals(dictionaryValue, foundDictionaryValue);
        verify(dictionaryValueRepository, times(1))
                .findByDictionaryIdAndName(1L, "Test Value");
    }

    @Test
    public void shouldThrowDictionaryValueNotFoundExceptionWhenDictionaryValueByDictionaryIdAndNameNotFound() {
        //given
        when(dictionaryValueRepository.findByDictionaryIdAndName(1L, "Test Value")).thenReturn(Optional.empty());

        //when/then
        DictionaryValueNotFoundException exception = assertThrows(DictionaryValueNotFoundException.class,
                () -> dictionaryValueService.getDictionaryValueByDictionaryIdAndName(1L, "Test Value"));
        assertEquals("Dictionary value 'Test Value' not found in the dictionary with id 1", exception.getMessage());
    }

    @Test
    public void shouldAddDictionaryValue() {
        //given
        CreateDictionaryValueCommand command = new CreateDictionaryValueCommand("New Value");
        DictionaryValue newValue = new DictionaryValue();
        newValue.setName("new value");
        when(dictionaryValueRepository.save(any(DictionaryValue.class))).thenReturn(newValue);

        //when
        DictionaryValue savedDictionaryValue = dictionaryValueService.addDictionaryValue(command);

        //then
        assertEquals("new value", savedDictionaryValue.getName());
        verify(dictionaryValueRepository, times(1)).save(any(DictionaryValue.class));
    }

    @Test
    public void shouldUpdateDictionaryValueName() {
        //given
        UpdateDictionaryValueCommand command = new UpdateDictionaryValueCommand(1L, "Updated Value");
        when(dictionaryValueRepository.findById(1L)).thenReturn(Optional.of(dictionaryValue));
        when(dictionaryValueRepository.save(dictionaryValue)).thenReturn(dictionaryValue);

        //when
        DictionaryValue updatedDictionaryValue = dictionaryValueService.updateDictionaryValueName(1L, command);

        //then
        assertEquals("updated value", updatedDictionaryValue.getName());
        verify(dictionaryValueRepository, times(1)).save(dictionaryValue);
    }

    @Test
    public void shouldThrowInvalidIdExceptionWhenUpdateDictionaryValueNameIdsDoNotMatch() {
        //given
        UpdateDictionaryValueCommand command = new UpdateDictionaryValueCommand(2L, "Updated Value");

        //when/then
        InvalidIdException exception = assertThrows(InvalidIdException.class,
                () -> dictionaryValueService.updateDictionaryValueName(1L, command));
        assertEquals("Identifiers provided in path variable and request body do not match", exception.getMessage());
    }

    @Test
    public void shouldDeleteDictionaryValueById() {
        //given
        when(dictionaryValueRepository.findById(1L)).thenReturn(Optional.of(dictionaryValue));

        //when
        dictionaryValueService.deleteDictionaryValueById(1L);

        //then
        verify(dictionaryValueRepository, times(1)).delete(dictionaryValue);
    }

    @Test
    public void shouldDeleteUnassignedValues() {
        //when
        dictionaryValueService.deleteUnassignedValues();

        //then
        verify(dictionaryValueRepository, times(1)).deleteUnassignedValues();
    }

}