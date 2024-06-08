package com.enjoythecode.userservice.controller;

import com.enjoythecode.userservice.command.CreateRoleCommand;
import com.enjoythecode.userservice.command.UpdateRoleCommand;
import com.enjoythecode.userservice.dto.RoleDto;
import com.enjoythecode.userservice.dto.StatusDto;
import com.enjoythecode.userservice.dto.UserSimpleDto;
import com.enjoythecode.userservice.exception.handler.ExceptionResponseBody;
import com.enjoythecode.userservice.model.AppRole;
import com.enjoythecode.userservice.service.AppRoleService;
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
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Validated
@Tag(name = "Roles", description = "Operations related to roles management")
public class AppRoleController {

    private final AppRoleService appRoleService;

    private final ModelMapper modelMapper;

    @GetMapping
    @Operation(summary = "Get all roles", description = "Retrieve all roles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = RoleDto.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class)))
    })
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        return ResponseEntity.ok(appRoleService.getAllRoles().stream()
                .map(x -> modelMapper.map(x, RoleDto.class))
                .collect(Collectors.toList()));
    }

    @GetMapping("/{roleId}")
    @Operation(summary = "Get role by ID", description = "Retrieve a role by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RoleDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid ID or Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class))),
            @ApiResponse(responseCode = "404", description = "Role not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class)))
    })
    public ResponseEntity<RoleDto> getRoleById(@PathVariable("roleId") Long roleId) {
        AppRole appRole = appRoleService.getRoleById(roleId);
        RoleDto roleDto = modelMapper.map(appRole, RoleDto.class);
        return ResponseEntity.ok(roleDto);
    }

    @PostMapping
    @Operation(summary = "Add a new role", description = "Create a new role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role successfully created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RoleDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class)))
    })
    public ResponseEntity<RoleDto> createRole(@RequestBody @Valid CreateRoleCommand command) {
        AppRole appRoleForSave = appRoleService.addRole(command);
        RoleDto roleDto = modelMapper.map(appRoleForSave, RoleDto.class);
        return ResponseEntity.ok(roleDto);
    }

    @PutMapping("/{roleId}")
    @Operation(summary = "Update name of role defined by ID", description = "Update the name of an existing role by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role successfully updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RoleDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid ID or Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class))),
            @ApiResponse(responseCode = "404", description = "Role not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class)))
    })
    public ResponseEntity<RoleDto> updateRole(@PathVariable("roleId") Long roleId,
                                              @RequestBody @Valid UpdateRoleCommand command) {
        AppRole updatedAppRole = appRoleService.updateRoleName(roleId, command);
        RoleDto roleDto = modelMapper.map(updatedAppRole, RoleDto.class);
        return ResponseEntity.ok(roleDto);
    }

    @DeleteMapping("/{roleId}")
    @Operation(summary = "Delete role by ID", description = "Delete a role by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role successfully deleted",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StatusDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid ID or Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class))),
            @ApiResponse(responseCode = "404", description = "Role not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class)))
    })
    public ResponseEntity<StatusDto> deleteRoleById(@PathVariable("roleId") Long roleId) {
        appRoleService.deleteRoleById(roleId);
        return ResponseEntity.ok(new StatusDto("Role with id " + roleId + " deleted."));
    }

    @GetMapping("/{roleId}/users")
    @Operation(summary = "Get all users assigned to the role defined by ID",
            description = "Retrieve all users assigned to a specific role by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserSimpleDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid ID or Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class))),
            @ApiResponse(responseCode = "404", description = "Role not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class)))
    })
    public ResponseEntity<List<UserSimpleDto>> getUsersByRole(@PathVariable Long roleId) {
        return ResponseEntity.ok(appRoleService.getUsersByRole(roleId).stream()
                .map(x -> modelMapper.map(x, UserSimpleDto.class))
                .collect(Collectors.toList()));
    }

}
