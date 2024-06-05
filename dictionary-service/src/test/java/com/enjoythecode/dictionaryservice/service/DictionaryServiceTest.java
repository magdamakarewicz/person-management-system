package com.enjoythecode.dictionaryservice.service;

import com.enjoythecode.dictionaryservice.command.CreateDictionaryCommand;
import com.enjoythecode.dictionaryservice.command.UpdateDictionaryCommand;
import com.enjoythecode.dictionaryservice.exception.DictionaryNotFoundException;
import com.enjoythecode.dictionaryservice.exception.DictionaryValueNotFoundException;
import com.enjoythecode.dictionaryservice.exception.IllegalDictionaryStateException;
import com.enjoythecode.dictionaryservice.exception.InvalidIdException;
import com.enjoythecode.dictionaryservice.model.Dictionary;
import com.enjoythecode.dictionaryservice.model.DictionaryValue;
import com.enjoythecode.dictionaryservice.repository.DictionaryRepository;
import com.enjoythecode.dictionaryservice.repository.DictionaryValueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DictionaryServiceTest {

    @Mock
    private DictionaryRepository dictionaryRepository;

    @Mock
    private DictionaryValueRepository dictionaryValueRepository;

    @Mock
    private DictionaryValueService dictionaryValueService;

    @InjectMocks
    private DictionaryService dictionaryService;

    private Dictionary dictionary;

    private DictionaryValue dictionaryValue1;

    private DictionaryValue dictionaryValue2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        dictionary = new Dictionary();
        dictionary.setId(1L);
        dictionary.setName("Test Dictionary");

        dictionaryValue1 = new DictionaryValue();
        dictionaryValue1.setId(1L);
        dictionaryValue1.setName("Test Value 1");
        dictionaryValue1.setDictionary(dictionary);

        dictionaryValue2 = new DictionaryValue();
        dictionaryValue2.setId(2L);
        dictionaryValue2.setName("Test Value 2");
        dictionaryValue2.setDictionary(dictionary);

        Set<DictionaryValue> dictionaryValues = new HashSet<>();
        dictionaryValues.add(dictionaryValue1);
        dictionaryValues.add(dictionaryValue2);
        dictionary.setDictionaryValues(dictionaryValues);
    }

    @Test
    public void shouldReturnDictionaryById() {
        //given
        when(dictionaryRepository.findByIdWithDictionaryValues(1L)).thenReturn(Optional.of(dictionary));

        //when
        Dictionary foundDictionary = dictionaryService.getDictionaryById(1L);

        //then
        assertEquals(dictionary, foundDictionary);
        verify(dictionaryRepository, atLeast(1)).findByIdWithDictionaryValues(1L);
    }

    @Test
    public void shouldThrowInvalidIdExceptionWhenIdIsNull() {
        //when/then
        InvalidIdException exception = assertThrows(InvalidIdException.class,
                () -> dictionaryService.getDictionaryById(null));
        assertEquals("Id cannot be null.", exception.getMessage());
    }

    @Test
    public void shouldThrowDictionaryNotFoundExceptionWhenDictionaryNotFound() {
        //given
        when(dictionaryRepository.findByIdWithDictionaryValues(1L)).thenReturn(Optional.empty());

        //when/then
        DictionaryNotFoundException exception = assertThrows(DictionaryNotFoundException.class,
                () -> dictionaryService.getDictionaryById(1L));
        assertEquals("Dictionary with id 1 not found.", exception.getMessage());
    }

    @Test
    public void shouldReturnAllDictionaries() {
        //given
        List<Dictionary> dictionaries = Collections.singletonList(dictionary);
        when(dictionaryRepository.findAll()).thenReturn(dictionaries);

        //when
        List<Dictionary> foundDictionaries = dictionaryService.getAllDictionaries();

        //then
        assertEquals(dictionaries, foundDictionaries);
        verify(dictionaryRepository, times(1)).findAll();
    }

    @Test
    public void shouldAddNewDictionary() {
        //given
        CreateDictionaryCommand command = new CreateDictionaryCommand("New Dictionary");
        when(dictionaryRepository.save(any(Dictionary.class))).thenReturn(dictionary);

        //when
        Dictionary savedDictionary = dictionaryService.addDictionary(command);

        //then
        assertEquals(dictionary, savedDictionary);
        verify(dictionaryRepository, times(1)).save(any(Dictionary.class));
    }

    @Test
    public void shouldUpdateDictionaryName() {
        //given
        UpdateDictionaryCommand command = new UpdateDictionaryCommand(1L, "Updated Dictionary");
        when(dictionaryRepository.findByIdWithDictionaryValues(1L)).thenReturn(Optional.of(dictionary));
        when(dictionaryRepository.save(dictionary)).thenReturn(dictionary);

        //when
        Dictionary updatedDictionary = dictionaryService.updateDictionaryName(1L, command);

        //then
        assertEquals("Updated Dictionary", updatedDictionary.getName());
        verify(dictionaryRepository, times(1)).save(dictionary);
    }

    @Test
    public void shouldThrowInvalidIdExceptionWhenUpdateDictionaryNameIdsDoNotMatch() {
        //given
        UpdateDictionaryCommand command = new UpdateDictionaryCommand(2L, "Updated Dictionary");

        //when/then
        InvalidIdException exception = assertThrows(InvalidIdException.class,
                () -> dictionaryService.updateDictionaryName(1L, command));
        assertEquals("Identifiers provided in path variable and request body do not match",
                exception.getMessage());
    }

    @Test
    public void shouldDeleteDictionaryById() {
        //given
        when(dictionaryRepository.findByIdWithDictionaryValues(1L)).thenReturn(Optional.of(dictionary));
        dictionary.getDictionaryValues().clear();

        //when
        dictionaryService.deleteDictionaryById(1L);

        //then
        verify(dictionaryRepository, times(1)).delete(dictionary);
    }

    @Test
    public void shouldThrowIllegalDictionaryStateExceptionWhenDeletingNonEmptyDictionary() {
        //given
        when(dictionaryRepository.findByIdWithDictionaryValues(1L)).thenReturn(Optional.of(dictionary));

        //when/then
        IllegalDictionaryStateException exception = assertThrows(IllegalDictionaryStateException.class,
                () -> dictionaryService.deleteDictionaryById(1L));
        assertEquals("A dictionary with values cannot be deleted. Delete dictionary values first.",
                exception.getMessage());
    }

    @Test
    public void shouldAddNewValueToDictionary() {
        //given
        DictionaryValue newValue = new DictionaryValue();
        newValue.setId(2L);
        newValue.setName("New Value");

        when(dictionaryRepository.findByIdWithDictionaryValues(1L)).thenReturn(Optional.of(dictionary));
        when(dictionaryValueService.getDictionaryValueById(2L)).thenReturn(newValue);
        when(dictionaryRepository.save(dictionary)).thenReturn(dictionary);

        //when
        Dictionary updatedDictionary = dictionaryService.addValueToDictionary(1L, 2L);

        //then
        assertTrue(updatedDictionary.getDictionaryValues().contains(newValue));
        verify(dictionaryRepository, times(1)).save(dictionary);
    }

    @Test
    public void shouldRemoveValueWithId1FromDictionary() {
        //given
        when(dictionaryRepository.findByIdWithDictionaryValues(1L)).thenReturn(Optional.of(dictionary));
        when(dictionaryValueService.getDictionaryValueById(1L)).thenReturn(dictionaryValue1);
        when(dictionaryRepository.save(any(Dictionary.class))).thenReturn(dictionary);

        //when
        Dictionary updatedDictionary = dictionaryService.removeValueFromDictionary(1L, 1L);

        //then
        assertFalse(updatedDictionary.getDictionaryValues().contains(dictionaryValue1));
        verify(dictionaryRepository, times(1)).save(dictionary);
    }

    @Test
    public void shouldThrowDictionaryValueNotFoundExceptionWhenRemovingNonExistingValue() {
        //given
        DictionaryValue nonExistingValue = new DictionaryValue();
        nonExistingValue.setId(2L);
        nonExistingValue.setName("Non Existing Value");

        when(dictionaryRepository.findByIdWithDictionaryValues(1L)).thenReturn(Optional.of(dictionary));
        when(dictionaryValueService.getDictionaryValueById(2L)).thenReturn(nonExistingValue);

        //when/then
        DictionaryValueNotFoundException exception = assertThrows(DictionaryValueNotFoundException.class,
                () -> dictionaryService.removeValueFromDictionary(1L, 2L));
        assertEquals("Dictionary does not contain the 'Non Existing Value' value.", exception.getMessage());
    }

}