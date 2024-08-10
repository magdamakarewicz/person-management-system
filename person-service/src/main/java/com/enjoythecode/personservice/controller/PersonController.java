package com.enjoythecode.personservice.controller;

import com.enjoythecode.personservice.command.CreatePersonCommand;
import com.enjoythecode.personservice.command.UpdatePersonCommand;
import com.enjoythecode.personservice.dto.ImportStatusDto;
import com.enjoythecode.personservice.dto.PersonDto;
import com.enjoythecode.personservice.dto.StatusDto;
import com.enjoythecode.personservice.exception.handler.ExceptionResponseBody;
import com.enjoythecode.personservice.factory.converter.PersonDtoConverterFactory;
import com.enjoythecode.personservice.model.ImportStatus;
import com.enjoythecode.personservice.model.Person;
import com.enjoythecode.personservice.service.DataImportFromCsvService;
import com.enjoythecode.personservice.service.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "People", description = "Operations related to people management")
public class PersonController {

    private final PersonService personService;
    private final DataImportFromCsvService dataImportFromCsvService;
    private final PersonDtoConverterFactory personDtoConverterFactory;
    private final ModelMapper modelMapper;

    @GetMapping
    @Operation(summary = "Get a list of people based on parameters", description = "This endpoint allows you to get " +
            "results based on parameters given in the URL after the '?'. Join conditions with the '&'. - for literal " +
            "parameters, enter: 'parameter=value', - for dictionary values parameter, enter dictionaryNameId=X, " +
            "where X is the id of demanded value (specified in the 'dictionaryName' dictionary), - for numerical " +
            "parameters, specify the range: 'parameter=fromX,toY', where X and Y are the limits of the closed range; " +
            "- for gender, specify 'sex=m' for a man and 'sex=w' for a woman. Provide in the URL 'typeId=X' parameter, " +
            "where X is the id of demanded type (specified in the 'type' dictionary) when filtering by a type-specific " +
            "parameter. Provide pageable if required: 'page=A&size=B, where A - page number, B - page size.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved person data",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PersonDto.class)))),
            @ApiResponse(responseCode = "401", description = "Authentication failed or user not authenticated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class)))
    })
    public ResponseEntity<List<PersonDto>> getPeople(@RequestParam Map<String, String> parameters,
                                                     @PageableDefault Pageable pageable) {
        Page<Person> people = personService.getPeople(parameters, pageable);
        List<PersonDto> personDtoList = people.stream()
                .map(personDtoConverterFactory::convert)
                .collect(Collectors.toList());
        return ResponseEntity.ok(personDtoList);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a person by ID", description = "Retrieve a person by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved person data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PersonDto.class))),
            @ApiResponse(responseCode = "404", description = "Person with the given ID not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class))),
            @ApiResponse(responseCode = "401", description = "Authentication failed or user not authenticated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class)))
    })
    public ResponseEntity<PersonDto> getPersonById(@PathVariable("id") Long id) {
        Person person = personService.getById(id);
        PersonDto personDto = personDtoConverterFactory.convert(person);
        return ResponseEntity.ok(personDto);
    }

    @PostMapping
    @Operation(summary = "Add a new person", description = "Create a new person record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully added new person",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PersonDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class))),
            @ApiResponse(responseCode = "401", description = "Authentication failed or user not authenticated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class)))
    })
    public ResponseEntity<PersonDto> createPerson(@RequestBody @Valid CreatePersonCommand createPeronCommand) {
        Person personForSave = personService.add(createPeronCommand);
        PersonDto personDto = personDtoConverterFactory.convert(personForSave);
        return ResponseEntity.ok(personDto);
    }

    @PostMapping("/type")
    @Operation(summary = "Add a new person type to 'type' dictionary", description = "Add a new type to the person type dictionary")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New person type added to 'type' dictionary",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StatusDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class))),
            @ApiResponse(responseCode = "401", description = "Authentication failed or user not authenticated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class)))
    })
    public ResponseEntity<StatusDto> createNewPersonType(@RequestParam String name) {
        personService.createNewType(name);
        return ResponseEntity.ok(new StatusDto("New person type '" + name + "' added to 'type' dictionary."));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update person data", description = "Update an existing person's data by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated person data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PersonDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class))),
            @ApiResponse(responseCode = "401", description = "Authentication failed or user not authenticated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class))),
            @ApiResponse(responseCode = "404", description = "Person with the given ID not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class)))
    })
    public ResponseEntity<PersonDto> updatePerson(@PathVariable("id") Long id,
                                                  @RequestBody @Valid UpdatePersonCommand updatePersonCommand) {
        Person personForUpdate = personService.edit(id, updatePersonCommand);
        PersonDto personDto = personDtoConverterFactory.convert(personForUpdate);
        return ResponseEntity.ok(personDto);
    }

    @PostMapping("/import")
    @Operation(summary = "Import data from a CSV file asynchronously",
            description = "This endpoint allows you to import data from a CSV file in an asynchronous manner. " +
                    "It processes the CSV file and saves the data to the database. Only one import can be performed at a time")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Data import has started. Check status endpoint /api/people/import/status for progress.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StatusDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class))),
            @ApiResponse(responseCode = "401", description = "Authentication failed or user not authenticated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class)))
    })
    public CompletableFuture<ResponseEntity<StatusDto>> importPeople(@RequestParam("file") MultipartFile file) {
        return dataImportFromCsvService.importPeopleFromCsvFile(file)
                .thenApply(result -> ResponseEntity.ok(new StatusDto("Data import has started. Check status endpoint " +
                        "/api/people/import/status for progress.")));
    }

    @GetMapping("/import/status")
    @Operation(summary = "Get data import status", description = "Retrieve the current status of the data import process")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Import status retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ImportStatusDto.class))),
            @ApiResponse(responseCode = "401", description = "Authentication failed or user not authenticated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class)))
    })
    public ResponseEntity<ImportStatusDto> getImportStatus() {
        ImportStatus importStatus = dataImportFromCsvService.getImportStatus();
        ImportStatusDto importStatusDto = modelMapper.map(importStatus, ImportStatusDto.class);
        return ResponseEntity.ok(importStatusDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete person by ID", description = "Delete a person by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Person with ID deleted",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StatusDto.class))),
            @ApiResponse(responseCode = "404", description = "Person with the given ID not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class))),
            @ApiResponse(responseCode = "401", description = "Authentication failed or user not authenticated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class)))
    })
    public ResponseEntity<StatusDto> deletePersonById(@PathVariable("id") Long id) {
        personService.deleteById(id);
        return ResponseEntity.ok(new StatusDto("Person with id " + id + " deleted"));
    }

}