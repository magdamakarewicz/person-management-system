package com.enjoythecode.userservice.controller;

import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.enjoythecode.userservice.command.CreateRoleCommand;
import com.enjoythecode.userservice.command.UpdateRoleCommand;
import com.enjoythecode.userservice.dto.RoleDto;
import com.enjoythecode.userservice.dto.StatusDto;
import com.enjoythecode.userservice.dto.UserSimpleDto;
import com.enjoythecode.userservice.model.AppRole;
import com.enjoythecode.userservice.service.AppRoleService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Validated
public class AppRoleController {

    private final AppRoleService appRoleService;

    private final ModelMapper modelMapper;

    @GetMapping
    @ApiOperation(value = "Get all roles", response = RoleDto.class, responseContainer = "List")
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        return ResponseEntity.ok(appRoleService.getAllRoles().stream()
                .map(x -> modelMapper.map(x, RoleDto.class))
                .collect(Collectors.toList()));
    }

    @GetMapping("/{roleId}")
    @ApiOperation(value = "Get role by ID", response = RoleDto.class)
    public ResponseEntity<RoleDto> getRoleById(@PathVariable("roleId") Long roleId) {
        AppRole appRole = appRoleService.getRoleById(roleId);
        RoleDto roleDto = modelMapper.map(appRole, RoleDto.class);
        return ResponseEntity.ok(roleDto);
    }

    @PostMapping
    @ApiOperation(value = "Add a new role", response = RoleDto.class)
    public ResponseEntity<RoleDto> createRole(@RequestBody @Valid CreateRoleCommand command) {
        AppRole appRoleForSave = appRoleService.addRole(command);
        RoleDto roleDto = modelMapper.map(appRoleForSave, RoleDto.class);
        return ResponseEntity.ok(roleDto);
    }

    @PutMapping("/{roleId}")
    @ApiOperation(value = "Update name of role defined by ID", response = RoleDto.class)
    public ResponseEntity<RoleDto> updateRole(@PathVariable("roleId") Long roleId,
                                              @RequestBody @Valid UpdateRoleCommand command) {
        AppRole updatedAppRole = appRoleService.updateRoleName(roleId, command);
        RoleDto roleDto = modelMapper.map(updatedAppRole, RoleDto.class);
        return ResponseEntity.ok(roleDto);
    }

    @DeleteMapping("/{roleId}")
    @ApiOperation(value = "Delete role by ID", response = StatusDto.class)
    public ResponseEntity<StatusDto> deleteRoleById(@PathVariable("roleId") Long roleId) {
        appRoleService.deleteRoleById(roleId);
        return ResponseEntity.ok(new StatusDto("Role with id " + roleId + " deleted."));
    }

    @GetMapping("/{roleId}/users")
    @ApiOperation(value = "Get all users assigned to the role defined by ID",
            response = UserSimpleDto.class, responseContainer = "List")
    public ResponseEntity<List<UserSimpleDto>> getUsersByRole(@PathVariable Long roleId) {
        return ResponseEntity.ok(appRoleService.getUsersByRole(roleId).stream()
                .map(x -> modelMapper.map(x, UserSimpleDto.class))
                .collect(Collectors.toList()));
    }

}
