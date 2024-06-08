package com.enjoythecode.dictionaryservice.controller;

import com.enjoythecode.dictionaryservice.command.CreateDictionaryCommand;
import com.enjoythecode.dictionaryservice.command.UpdateDictionaryCommand;
import com.enjoythecode.dictionaryservice.dto.DictionaryDto;
import com.enjoythecode.dictionaryservice.dto.DictionarySimpleDto;
import com.enjoythecode.dictionaryservice.dto.DictionaryValueSimpleDto;
import com.enjoythecode.dictionaryservice.dto.StatusDto;
import com.enjoythecode.dictionaryservice.exception.handler.ExceptionResponseBody;
import com.enjoythecode.dictionaryservice.model.Dictionary;
import com.enjoythecode.dictionaryservice.service.DictionaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "DictionaryController",
        description = "The application provides a system that stores dictionaries with the values stored in them.")
public class DictionaryController {

    private final DictionaryService dictionaryService;

    private final ModelMapper modelMapper;

    @GetMapping
    @Operation(summary = "Get all dictionaries", description = "Retrieve a list of all dictionaries")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = DictionarySimpleDto.class))))
    })
    public ResponseEntity<List<DictionarySimpleDto>> getAllDictionaries() {
        return ResponseEntity.ok(dictionaryService.getAllDictionaries().stream()
                .map(x -> modelMapper.map(x, DictionarySimpleDto.class))
                .collect(Collectors.toList()));
    }

    @GetMapping("/{dictionaryId}")
    @Operation(summary = "Get dictionary by ID", description = "Retrieve a dictionary by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DictionarySimpleDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid ID or Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class))),
            @ApiResponse(responseCode = "404", description = "Dictionary not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class)))
    })
    public ResponseEntity<DictionarySimpleDto> getDictionaryById(@PathVariable("dictionaryId") Long dictionaryId) {
        Dictionary dictionary = dictionaryService.getDictionaryById(dictionaryId);
        DictionarySimpleDto dictionarySimpleDto = modelMapper.map(dictionary, DictionarySimpleDto.class);
        return ResponseEntity.ok(dictionarySimpleDto);
    }

    @PostMapping
    @Operation(summary = "Add a new dictionary", description = "Create a new dictionary")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful creation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DictionarySimpleDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid or bad request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class)))
    })
    public ResponseEntity<DictionarySimpleDto> createDictionary(@RequestBody @Valid CreateDictionaryCommand command) {
        Dictionary dictionaryForSave = dictionaryService.addDictionary(command);
        DictionarySimpleDto dictionarySimpleDto = modelMapper.map(dictionaryForSave, DictionarySimpleDto.class);
        return ResponseEntity.ok(dictionarySimpleDto);
    }

    @PutMapping("/{dictionaryId}")
    @Operation(summary = "Update name of dictionary", description = "Update the name of a dictionary by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful update",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DictionarySimpleDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid or bad request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class))),
            @ApiResponse(responseCode = "404", description = "Dictionary not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class)))
    })
    public ResponseEntity<DictionarySimpleDto> updateDictionaryName(@PathVariable("dictionaryId") Long dictionaryId,
                                                                    @RequestBody @Valid UpdateDictionaryCommand command) {
        Dictionary updatedDictionary = dictionaryService.updateDictionaryName(dictionaryId, command);
        DictionarySimpleDto dictionarySimpleDto = modelMapper.map(updatedDictionary, DictionarySimpleDto.class);
        return ResponseEntity.ok(dictionarySimpleDto);
    }

    @DeleteMapping("/{dictionaryId}")
    @Operation(summary = "Delete dictionary by ID", description = "Delete a dictionary by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful deletion",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StatusDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class))),
            @ApiResponse(responseCode = "404", description = "Dictionary not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class)))
    })
    public ResponseEntity<StatusDto> deleteDictionaryById(@PathVariable("dictionaryId") Long dictionaryId) {
        dictionaryService.deleteDictionaryById(dictionaryId);
        return ResponseEntity.ok(new StatusDto("Dictionary with id " + dictionaryId + " deleted."));
    }

    @PostMapping("/{dictionaryId}/values/{dictionaryValueId}")
    @Operation(summary = "Add a value to dictionary",
            description = "Add a value defined by ID to an existing dictionary defined by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful addition",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DictionaryDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid or bad request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class))),
            @ApiResponse(responseCode = "404", description = "Dictionary or value not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class)))
    })
    public ResponseEntity<DictionaryDto> addValueToDictionary(@PathVariable Long dictionaryId,
                                                              @PathVariable Long dictionaryValueId) {
        Dictionary updatedDictionary = dictionaryService.addValueToDictionary(dictionaryId, dictionaryValueId);
        DictionaryDto dictionaryDto = modelMapper.map(updatedDictionary, DictionaryDto.class);
        return ResponseEntity.ok(dictionaryDto);
    }

    @PostMapping("/1/values")
    @Operation(summary = "Add a value to type dictionary",
            description = "Add a value defined by name to 'type' dictionary which ID = 1")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful addition",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DictionarySimpleDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid or bad request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class))),
            @ApiResponse(responseCode = "404", description = "Dictionary not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class)))
    })
    public ResponseEntity<DictionarySimpleDto> addValueToTypeDictionary(@RequestParam String name) {
        Dictionary updatedDictionary = dictionaryService.addValueByNameToTypeDictionary(name);
        DictionarySimpleDto dictionaryDto = modelMapper.map(updatedDictionary, DictionarySimpleDto.class);
        return ResponseEntity.ok(dictionaryDto);
    }

    @DeleteMapping("/{dictionaryId}/values/{dictionaryValueId}")
    @Operation(summary = "Remove a value from dictionary",
            description = "Remove a value defined by ID from dictionary defined by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful removal",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DictionaryDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid or bad request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class))),
            @ApiResponse(responseCode = "404", description = "Dictionary or value not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class)))
    })
    public ResponseEntity<DictionaryDto> removeValueFromDictionary(@PathVariable Long dictionaryId, @PathVariable Long dictionaryValueId) {
        Dictionary updatedDictionary = dictionaryService.removeValueFromDictionary(dictionaryId, dictionaryValueId);
        DictionaryDto dictionaryDto = modelMapper.map(updatedDictionary, DictionaryDto.class);
        return ResponseEntity.ok(dictionaryDto);
    }

    @DeleteMapping("/{dictionaryId}/values")
    @Operation(summary = "Remove all values from dictionary", description = "Remove all values from dictionary defined by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful removal",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StatusDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid or bad request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class))),
            @ApiResponse(responseCode = "404", description = "Dictionary not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class)))
    })
    public ResponseEntity<StatusDto> removeAllValuesFromDictionary(@PathVariable Long dictionaryId) {
        dictionaryService.deleteValuesFromDictionary(dictionaryId);
        return ResponseEntity.ok(new StatusDto("Values from dictionary with id " + dictionaryId + " deleted."));
    }

    @GetMapping("/{dictionaryId}/values")
    @Operation(summary = "Get all dictionary values",
            description = "Get all dictionary values from the dictionary defined by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DictionaryValueSimpleDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid or bad request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class))),
            @ApiResponse(responseCode = "404", description = "Dictionary not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class)))
    })
    public ResponseEntity<List<DictionaryValueSimpleDto>> getValuesByDictionaryId(@PathVariable Long dictionaryId) {
        return ResponseEntity.ok(dictionaryService.getValuesByDictionaryId(dictionaryId).stream()
                .map(x -> modelMapper.map(x, DictionaryValueSimpleDto.class))
                .collect(Collectors.toList()));
    }

}
