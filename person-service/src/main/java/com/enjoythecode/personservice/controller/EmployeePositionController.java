package com.enjoythecode.personservice.controller;

import com.enjoythecode.personservice.command.CreateEmployeePositionCommand;
import com.enjoythecode.personservice.command.UpdateEmployeePositionEndDateCommand;
import com.enjoythecode.personservice.dto.EmployeePositionDto;
import com.enjoythecode.personservice.dto.EmployeePositionFullDto;
import com.enjoythecode.personservice.dto.StatusDto;
import com.enjoythecode.personservice.exception.handler.ExceptionResponseBody;
import com.enjoythecode.personservice.model.EmployeePosition;
import com.enjoythecode.personservice.service.EmployeePositionService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employees/{employeeId}/positions")
@RequiredArgsConstructor
@Validated
@Tag(name = "Employee Positions", description = "Operations related to employee positions management")
public class EmployeePositionController {

    private final EmployeePositionService employeePositionService;

    private final ModelMapper modelMapper;

    @PostMapping
    @Operation(summary = "Add a new position to an employee",
            description = "Create a new position for the specified employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully added new position",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EmployeePositionFullDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class))),
            @ApiResponse(responseCode = "401", description = "Authentication failed or user not authenticated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class))),
            @ApiResponse(responseCode = "409", description = "Conflict in data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class)))
    })
    public ResponseEntity<EmployeePositionFullDto> addNewPositionToEmployee(
            @PathVariable Long employeeId,
            @RequestBody @Valid CreateEmployeePositionCommand command) {
        EmployeePosition employeePositionToSave = employeePositionService.addPositionToEmployee(employeeId, command);
        EmployeePositionFullDto employeePositionDto = modelMapper.map(employeePositionToSave, EmployeePositionFullDto.class);
        return ResponseEntity.ok(employeePositionDto);
    }

    @PatchMapping("/{positionId}")
    @Operation(summary = "Update the end date for the current position",
            description = "Update the end date of the specified position for the specified employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated position end date",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EmployeePositionDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class))),
            @ApiResponse(responseCode = "401", description = "Authentication failed or user not authenticated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class))),
            @ApiResponse(responseCode = "404", description = "Position or employee not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class)))
    })
    public ResponseEntity<EmployeePositionDto> updateEndDateForCurrentPosition(
            @PathVariable Long employeeId,
            @PathVariable Long positionId,
            @RequestBody @Valid UpdateEmployeePositionEndDateCommand command) {
        EmployeePosition updatedPosition = employeePositionService.updateEndDateForCurrentPosition(
                employeeId, positionId, command);
        EmployeePositionDto positionDto = modelMapper.map(updatedPosition, EmployeePositionDto.class);
        return ResponseEntity.ok(positionDto);
    }

    @GetMapping
    @Operation(summary = "Get all employee positions", description = "Retrieve all positions for the specified employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved positions",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = EmployeePositionDto.class)))),
            @ApiResponse(responseCode = "401", description = "Authentication failed or user not authenticated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class))),
            @ApiResponse(responseCode = "404", description = "Employee not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class)))
    })
    public ResponseEntity<List<EmployeePositionDto>> getAllEmployeePositions(
            @PathVariable Long employeeId) {
        return ResponseEntity.ok(employeePositionService.getEmployeePositions(employeeId).stream()
                .map(x -> modelMapper.map(x, EmployeePositionDto.class))
                .collect(Collectors.toList()));
    }

    @GetMapping("/{positionId}")
    @Operation(summary = "Get an employee position by ID",
            description = "Retrieve a specific position for the specified employee by position ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved position",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EmployeePositionDto.class))),
            @ApiResponse(responseCode = "401", description = "Authentication failed or user not authenticated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class))),
            @ApiResponse(responseCode = "404", description = "Position or employee not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class)))
    })
    public ResponseEntity<EmployeePositionDto> getEmployeePosition(
            @PathVariable Long employeeId,
            @PathVariable Long positionId) {
        EmployeePosition employeePosition = employeePositionService.getEmployeePositionById(employeeId, positionId);
        EmployeePositionDto positionDto = modelMapper.map(employeePosition, EmployeePositionDto.class);
        return ResponseEntity.ok(positionDto);
    }

    @DeleteMapping("/{positionId}")
    @Operation(summary = "Delete an employee position by ID",
            description = "Delete a specific position for the specified employee by position ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted position",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StatusDto.class))),
            @ApiResponse(responseCode = "401", description = "Authentication failed or user not authenticated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class))),
            @ApiResponse(responseCode = "404", description = "Position or employee not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class)))
    })
    public ResponseEntity<StatusDto> deletePositionById(
            @PathVariable Long employeeId,
            @PathVariable("positionId") Long positionId) {
        employeePositionService.deleteById(employeeId, positionId);
        return ResponseEntity.ok(new StatusDto(
                "Position with id " + positionId + " deleted " + "from employee with id " + employeeId));
    }

}