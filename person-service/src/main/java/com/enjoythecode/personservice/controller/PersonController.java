package com.enjoythecode.personservice.controller;

import com.enjoythecode.personservice.command.CreatePersonCommand;
import com.enjoythecode.personservice.command.UpdatePersonCommand;
import com.enjoythecode.personservice.dto.ImportStatusDto;
import com.enjoythecode.personservice.dto.PersonDto;
import com.enjoythecode.personservice.dto.StatusDto;
import com.enjoythecode.personservice.factory.converter.PersonDtoConverterFactory;
import com.enjoythecode.personservice.model.ImportStatus;
import com.enjoythecode.personservice.model.Person;
import com.enjoythecode.personservice.service.DataImportFromCsvService;
import com.enjoythecode.personservice.service.PersonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/people")
@RequiredArgsConstructor
@Validated
@Api(value = "Person Controller")
public class PersonController {

    private final PersonService personService;
    private final DataImportFromCsvService dataImportFromCsvService;
    private final PersonDtoConverterFactory personDtoConverterFactory;

    @GetMapping
    @ApiOperation(value = "Get a list of people based on parameters",
            notes = "This endpoint allows you to get results based on parameters given in the URL after the '?'." +
                    "Join conditions with the '&'." +
                    "- for literal parameters, enter: 'parameter=value'," +
                    "- for dictionary values parameter, enter dictionaryNameId=X, where X is the id of demanded" +
                    "value (specified in the 'dictionaryName' dictionary," +
                    "- for numerical parameters, specify the range: 'parameter=fromX,toY', where X and Y " +
                    "are the limits of the closed range;" +
                    "- for gender, specify 'sex=m' for a man and 'sex=w' for a woman." +
                    "Provide in the URL 'typeId=X' parameter, where X is the id of demanded type (specified in " +
                    "the 'type' dictionary) when filtering by a type-specific parameter." +
                    "Provide pageable if required: 'page=A&size=B, where A - page number, B - page size.",
            response = PersonDto.class,
            responseContainer = "List")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully retrieved person data"),
            @ApiResponse(code = 401, message = "Authentication failed or user not authenticated"),
    })
    public ResponseEntity<List<PersonDto>> getPeople(@RequestParam Map<String, String> parameters,
                                                     @PageableDefault Pageable pageable) {
        Page<Person> people = personService.getPeople(parameters, pageable);
        List<PersonDto> personDtoList = people.stream()
                .map(personDtoConverterFactory::convert)
                .collect(Collectors.toList());
        return ResponseEntity.ok(personDtoList);
    }

    private final ModelMapper modelMapper;

    @GetMapping("/{id}")
    @ApiOperation(value = "Get a person by ID", response = PersonDto.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully retrieved person data"),
            @ApiResponse(code = 404, message = "Person with the given ID not found"),
            @ApiResponse(code = 401, message = "Authentication failed or user not authenticated"),
    })
    public ResponseEntity<PersonDto> getPersonById(@PathVariable("id") Long id) {
        Person person = personService.getById(id);
        PersonDto personDto = personDtoConverterFactory.convert(person);
        return ResponseEntity.ok(personDto);
    }

    @PostMapping
    @ApiOperation(value = "Add a new person", response = PersonDto.class)
    public ResponseEntity<PersonDto> createPerson(@RequestBody @Valid CreatePersonCommand createPeronCommand) {
        Person personForSave = personService.add(createPeronCommand);
        PersonDto personDto = personDtoConverterFactory.convert(personForSave);
        return ResponseEntity.ok(personDto);
    }

    @PostMapping("/type")
    @ApiOperation(value = "Add a new person type to 'type' dictionary", response = PersonDto.class)
    public ResponseEntity<StatusDto> createNewPersonType(@RequestParam String name) {
        personService.createNewType(name);
        return ResponseEntity.ok(new StatusDto("New person type '" + name + "' added to 'type' dictionary."));
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Update person data", response = PersonDto.class)
    public ResponseEntity<PersonDto> updatePerson(@PathVariable("id") Long id,
                                                  @RequestBody @Valid UpdatePersonCommand updatePersonCommand) {
        Person personForUpdate = personService.edit(id, updatePersonCommand);
        PersonDto personDto = personDtoConverterFactory.convert(personForUpdate);
        return ResponseEntity.ok(personDto);
    }

    @PostMapping("/import")
    @ApiOperation(value = "Import data from a CSV file asynchronously",
            notes = "This endpoint allows you to import data from a CSV file in an asynchronous manner. " +
                    "It processes the CSV file and saves the data to the database. " +
                    "Only one import can be performed at a time", response = StatusDto.class)
    public CompletableFuture<ResponseEntity<StatusDto>> importPeople(@RequestParam("file") MultipartFile file) {
        return dataImportFromCsvService.importPeopleFromCsvFile(file)
                .thenApply(result -> ResponseEntity.ok(new StatusDto("Data import has started. Check status endpoint " +
                        "/api/people/import/status for progress.")));
    }

    @GetMapping("/import/status")
    @ApiOperation(value = "Get data import status", response = ImportStatusDto.class)
    public ResponseEntity<ImportStatusDto> getImportStatus() {
        ImportStatus importStatus = dataImportFromCsvService.getImportStatus();
        ImportStatusDto importStatusDto = modelMapper.map(importStatus, ImportStatusDto.class);
        return ResponseEntity.ok(importStatusDto);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete person by ID", response = StatusDto.class)
    public ResponseEntity<StatusDto> deletePersonById(@PathVariable("id") Long id) {
        personService.deleteById(id);
        return ResponseEntity.ok(new StatusDto("Person with id " + id + " deleted"));
    }

}