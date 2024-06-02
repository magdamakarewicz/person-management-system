package com.enjoythecode.dictionaryservice.controller;

import com.enjoythecode.dictionaryservice.command.CreateDictionaryCommand;
import com.enjoythecode.dictionaryservice.command.UpdateDictionaryCommand;
import com.enjoythecode.dictionaryservice.dto.DictionaryDto;
import com.enjoythecode.dictionaryservice.dto.DictionarySimpleDto;
import com.enjoythecode.dictionaryservice.dto.DictionaryValueSimpleDto;
import com.enjoythecode.dictionaryservice.dto.StatusDto;
import com.enjoythecode.dictionaryservice.model.Dictionary;
import com.enjoythecode.dictionaryservice.service.DictionaryService;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dictionaries")
@AllArgsConstructor
@Validated
public class DictionaryController {

    private final DictionaryService dictionaryService;

    private final ModelMapper modelMapper;

    @GetMapping
    @ApiOperation(value = "Get all dictionaries", response = DictionarySimpleDto.class, responseContainer = "List")
    public ResponseEntity<List<DictionarySimpleDto>> getAllDictionaries() {
        return ResponseEntity.ok(dictionaryService.getAllDictionaries().stream()
                .map(x -> modelMapper.map(x, DictionarySimpleDto.class))
                .collect(Collectors.toList()));
    }

    @GetMapping("/{dictionaryId}")
    @ApiOperation(value = "Get dictionary by ID", response = DictionarySimpleDto.class)
    public ResponseEntity<DictionarySimpleDto> getDictionaryById(@PathVariable("dictionaryId") Long dictionaryId) {
        Dictionary dictionary = dictionaryService.getDictionaryById(dictionaryId);
        DictionarySimpleDto dictionarySimpleDto = modelMapper.map(dictionary, DictionarySimpleDto.class);
        return ResponseEntity.ok(dictionarySimpleDto);
    }

    @PostMapping
    @ApiOperation(value = "Add a new dictionary", response = DictionarySimpleDto.class)
    public ResponseEntity<DictionarySimpleDto> createDictionary(@RequestBody @Valid CreateDictionaryCommand command) {
        Dictionary dictionaryForSave = dictionaryService.addDictionary(command);
        DictionarySimpleDto dictionarySimpleDto = modelMapper.map(dictionaryForSave, DictionarySimpleDto.class);
        return ResponseEntity.ok(dictionarySimpleDto);
    }

    @PutMapping("/{dictionaryId}")
    @ApiOperation(value = "Update name of dictionary", response = DictionarySimpleDto.class)
    public ResponseEntity<DictionarySimpleDto> updateDictionaryName(@PathVariable("dictionaryId") Long dictionaryId,
                                                                    @RequestBody @Valid UpdateDictionaryCommand command) {
        Dictionary updatedDictionary = dictionaryService.updateDictionaryName(dictionaryId, command);
        DictionarySimpleDto dictionarySimpleDto = modelMapper.map(updatedDictionary, DictionarySimpleDto.class);
        return ResponseEntity.ok(dictionarySimpleDto);
    }

    @DeleteMapping("/{dictionaryId}")
    @ApiOperation(value = "Delete dictionary by ID", response = StatusDto.class)
    public ResponseEntity<StatusDto> deleteDictionaryById(@PathVariable("dictionaryId") Long dictionaryId) {
        dictionaryService.deleteDictionaryById(dictionaryId);
        return ResponseEntity.ok(new StatusDto("Dictionary with id " + dictionaryId + " deleted."));
    }

    @PostMapping("/{dictionaryId}/values/{dictionaryValueId}")
    @ApiOperation(value = "Add a value defined by ID to existing dictionary defined by ID",
            response = DictionaryDto.class)
    public ResponseEntity<DictionaryDto> addValueToDictionary(@PathVariable Long dictionaryId,
                                                              @PathVariable Long dictionaryValueId) {
        Dictionary updatedDictionary = dictionaryService.addValueToDictionary(dictionaryId, dictionaryValueId);
        DictionaryDto dictionaryDto = modelMapper.map(updatedDictionary, DictionaryDto.class);
        return ResponseEntity.ok(dictionaryDto);
    }

    @PostMapping("/1/values")
    @ApiOperation(value = "Add a value defined by name to 'type' dictionary which ID = 1",
            response = DictionarySimpleDto.class)
    public ResponseEntity<DictionarySimpleDto> addValueToTypeDictionary(@RequestParam String name) {
        Dictionary updatedDictionary = dictionaryService.addValueByNameToTypeDictionary(name);
        DictionarySimpleDto dictionaryDto = modelMapper.map(updatedDictionary, DictionarySimpleDto.class);
        return ResponseEntity.ok(dictionaryDto);
    }

    @DeleteMapping("/{dictionaryId}/values/{dictionaryValueId}")
    @ApiOperation(value = "Remove a value defined by ID from dictionary defined by ID", response = DictionaryDto.class)
    public ResponseEntity<DictionaryDto> removeValueFromDictionary(@PathVariable Long dictionaryId, @PathVariable Long dictionaryValueId) {
        Dictionary updatedDictionary = dictionaryService.removeValueFromDictionary(dictionaryId, dictionaryValueId);
        DictionaryDto dictionaryDto = modelMapper.map(updatedDictionary, DictionaryDto.class);
        return ResponseEntity.ok(dictionaryDto);
    }

    @DeleteMapping("/{dictionaryId}/values")
    @ApiOperation(value = "Remove all values from dictionary defined by ID", response = StatusDto.class)
    public ResponseEntity<StatusDto> removeAllValuesFromDictionary(@PathVariable Long dictionaryId) {
        dictionaryService.deleteValuesFromDictionary(dictionaryId);
        return ResponseEntity.ok(new StatusDto("Values from dictionary with id " + dictionaryId + " deleted."));
    }

    @GetMapping("/{dictionaryId}/values")
    @ApiOperation(value = "Get all dictionary values from the dictionary defined by ID",
            response = DictionaryValueSimpleDto.class, responseContainer = "List")
    public ResponseEntity<List<DictionaryValueSimpleDto>> getValuesByDictionaryId(@PathVariable Long dictionaryId) {
        return ResponseEntity.ok(dictionaryService.getValuesByDictionaryId(dictionaryId).stream()
                .map(x -> modelMapper.map(x, DictionaryValueSimpleDto.class))
                .collect(Collectors.toList()));
    }

}
