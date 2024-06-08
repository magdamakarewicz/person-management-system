package com.enjoythecode.dictionaryservice.controller;

import com.enjoythecode.dictionaryservice.command.CreateDictionaryValueCommand;
import com.enjoythecode.dictionaryservice.command.UpdateDictionaryValueCommand;
import com.enjoythecode.dictionaryservice.dto.DictionaryValueDto;
import com.enjoythecode.dictionaryservice.dto.DictionaryValueSimpleDto;
import com.enjoythecode.dictionaryservice.dto.StatusDto;
import com.enjoythecode.dictionaryservice.exception.handler.ExceptionResponseBody;
import com.enjoythecode.dictionaryservice.model.DictionaryValue;
import com.enjoythecode.dictionaryservice.service.DictionaryValueService;
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
@RequestMapping("/api/dictionaryvalues")
@AllArgsConstructor
@Validated
@Tag(name = "DictionaryValueController",
        description = "Controller for managing dictionary values within the application.")
public class DictionaryValueController {

    private final DictionaryValueService dictionaryValueService;

    private final ModelMapper modelMapper;

    @GetMapping
    @Operation(summary = "Get all dictionary values", description = "Retrieve a list of all dictionary values")
    @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = DictionaryValueSimpleDto.class))))
    public ResponseEntity<List<DictionaryValueSimpleDto>> getAllDictionaryValues() {
        return ResponseEntity.ok(dictionaryValueService.getAllDictionaryValues().stream()
                .map(x -> modelMapper.map(x, DictionaryValueSimpleDto.class))
                .collect(Collectors.toList()));
    }

    @GetMapping("/{dictionaryValueId}")
    @Operation(summary = "Get dictionary value by ID", description = "Retrieve a dictionary value by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DictionaryValueSimpleDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid ID or Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class))),
            @ApiResponse(responseCode = "404", description = "Dictionary value not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class)))
    })
    public ResponseEntity<DictionaryValueSimpleDto> getDictionaryValueById(
            @PathVariable("dictionaryValueId") Long dictionaryValueId) {
        DictionaryValue dictionaryValue = dictionaryValueService.getDictionaryValueById(dictionaryValueId);
        DictionaryValueSimpleDto dictionaryValueDto = modelMapper.map(dictionaryValue, DictionaryValueSimpleDto.class);
        return ResponseEntity.ok(dictionaryValueDto);
    }

    @GetMapping("/{dictionaryId}/value")
    @Operation(summary = "Get dictionary value by dictionaryId and value name",
            description = "Retrieve a dictionary value by dictionaryId and value name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DictionaryValueSimpleDto.class))),
            @ApiResponse(responseCode = "404", description = "Dictionary value not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class)))
    })
    public ResponseEntity<DictionaryValueSimpleDto> getDictionaryValueByDictionaryIdAndName(
            @PathVariable("dictionaryId") Long dictionaryId, @RequestParam String name) {
        DictionaryValue dictionaryValue = dictionaryValueService.getDictionaryValueByDictionaryIdAndName(dictionaryId, name);
        DictionaryValueSimpleDto dictionaryValueDto = modelMapper.map(dictionaryValue, DictionaryValueSimpleDto.class);
        return ResponseEntity.ok(dictionaryValueDto);
    }

    @PostMapping
    @Operation(summary = "Add a new dictionary value", description = "Create a new dictionary value")
    @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DictionaryValueDto.class)))
    public ResponseEntity<DictionaryValueDto> createDictionaryValue(@RequestBody @Valid CreateDictionaryValueCommand command) {
        DictionaryValue dictionaryValueForSave = dictionaryValueService.addDictionaryValue(command);
        DictionaryValueDto dictionaryValueDto = modelMapper.map(dictionaryValueForSave, DictionaryValueDto.class);
        return ResponseEntity.ok(dictionaryValueDto);
    }

    @PutMapping("/{dictionaryValueId}")
    @Operation(summary = "Update name of dictionary value",
            description = "Update the name of an existing dictionary value")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DictionaryValueSimpleDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid ID or Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class))),
            @ApiResponse(responseCode = "404", description = "Dictionary value not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class)))
    })
    public ResponseEntity<DictionaryValueDto> updateValueName(@PathVariable("dictionaryValueId") Long dictionaryValueId,
                                                              @RequestBody @Valid UpdateDictionaryValueCommand command) {
        DictionaryValue updatedDictionaryValue = dictionaryValueService.updateDictionaryValueName(dictionaryValueId, command);
        DictionaryValueDto dictionaryValueDto = modelMapper.map(updatedDictionaryValue, DictionaryValueDto.class);
        return ResponseEntity.ok(dictionaryValueDto);
    }

    @DeleteMapping("/{dictionaryValueId}")
    @Operation(summary = "Delete dictionary value by ID", description = "Delete a dictionary value by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DictionaryValueSimpleDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid ID or Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class))),
            @ApiResponse(responseCode = "404", description = "Dictionary value not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class)))
    })
    public ResponseEntity<StatusDto> deleteDictionaryValueById(@PathVariable("dictionaryValueId") Long dictionaryValueId) {
        dictionaryValueService.deleteDictionaryValueById(dictionaryValueId);
        return ResponseEntity.ok(new StatusDto("Dictionary value with id " + dictionaryValueId + " deleted."));
    }

    @DeleteMapping("/unassigned")
    @Operation(summary = "Delete unassigned dictionary values", description = "Delete all unassigned dictionary values")
    @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StatusDto.class)))
    public ResponseEntity<StatusDto> deleteUnassignedDictionaryValues() {
        dictionaryValueService.deleteUnassignedValues();
        return ResponseEntity.ok(new StatusDto("Unassigned dictionary values deleted."));
    }

}
