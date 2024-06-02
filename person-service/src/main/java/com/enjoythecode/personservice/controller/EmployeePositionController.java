package com.enjoythecode.personservice.controller;

import com.enjoythecode.personservice.command.CreateEmployeePositionCommand;
import com.enjoythecode.personservice.command.UpdateEmployeePositionEndDateCommand;
import com.enjoythecode.personservice.dto.EmployeePositionDto;
import com.enjoythecode.personservice.dto.EmployeePositionFullDto;
import com.enjoythecode.personservice.dto.StatusDto;
import com.enjoythecode.personservice.model.EmployeePosition;
import com.enjoythecode.personservice.service.EmployeePositionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(value = "Employee Position Controller")
public class EmployeePositionController {

    private final EmployeePositionService employeePositionService;

    private final ModelMapper modelMapper;

    @PostMapping
    @ApiOperation(value = "Add a new position to an employee", response = EmployeePositionFullDto.class)
    public ResponseEntity<EmployeePositionFullDto> addNewPositionToEmployee(
            @PathVariable Long employeeId,
            @RequestBody @Valid CreateEmployeePositionCommand command) {
        EmployeePosition employeePositionToSave = employeePositionService.addPositionToEmployee(employeeId, command);
        EmployeePositionFullDto employeePositionDto = modelMapper.map(employeePositionToSave, EmployeePositionFullDto.class);
        return ResponseEntity.ok(employeePositionDto);
    }

    @PatchMapping("/{positionId}")
    @ApiOperation(value = "Update the end date for the current position", response = EmployeePositionDto.class)
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
    @ApiOperation(value = "Get all employee positions", response = EmployeePositionDto.class, responseContainer = "List")
    public ResponseEntity<List<EmployeePositionDto>> getAllEmployeePositions(
            @PathVariable Long employeeId) {
        return ResponseEntity.ok(employeePositionService.getEmployeePositions(employeeId).stream()
                .map(x -> modelMapper.map(x, EmployeePositionDto.class))
                .collect(Collectors.toList()));
    }

    @GetMapping("/{positionId}")
    @ApiOperation(value = "Get an employee position by ID", response = EmployeePositionDto.class)
    public ResponseEntity<EmployeePositionDto> getEmployeePosition(
            @PathVariable Long employeeId,
            @PathVariable Long positionId) {
        EmployeePosition employeePosition = employeePositionService.getEmployeePositionById(employeeId, positionId);
        EmployeePositionDto positionDto = modelMapper.map(employeePosition, EmployeePositionDto.class);
        return ResponseEntity.ok(positionDto);
    }

    @DeleteMapping("/{positionId}")
    @ApiOperation(value = "Delete an employee position by ID", response = StatusDto.class)
    public ResponseEntity<StatusDto> deletePositionById(
            @PathVariable Long employeeId,
            @PathVariable("positionId") Long positionId) {
        employeePositionService.deleteById(employeeId, positionId);
        return ResponseEntity.ok(new StatusDto(
                "Position with id " + positionId + " deleted " + "from employee with id " + employeeId));
    }

}