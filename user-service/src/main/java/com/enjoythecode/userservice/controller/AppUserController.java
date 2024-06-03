package com.enjoythecode.userservice.controller;

import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.enjoythecode.userservice.command.CreateUserCommand;
import com.enjoythecode.userservice.command.UpdateUserPasswordCommand;
import com.enjoythecode.userservice.dto.StatusDto;
import com.enjoythecode.userservice.dto.UserDto;
import com.enjoythecode.userservice.dto.UserSimpleDto;
import com.enjoythecode.userservice.model.AppUser;
import com.enjoythecode.userservice.service.AppUserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class AppUserController {

    private final AppUserService appUserService;

    private final ModelMapper modelMapper;

    @GetMapping
    @ApiOperation(value = "Get all users", response = UserSimpleDto.class, responseContainer = "List")
    public ResponseEntity<List<UserSimpleDto>> getAllUsers() {
        return ResponseEntity.ok(appUserService.getAllUsers().stream()
                .map(x -> modelMapper.map(x, UserSimpleDto.class))
                .collect(Collectors.toList()));
    }

    @GetMapping("/{userId}")
    @ApiOperation(value = "Get user by ID", response = UserDto.class)
    public ResponseEntity<UserDto> getUserById(@PathVariable("userId") Long userId) {
        AppUser appUser = appUserService.getUserById(userId);
        UserDto userDto = modelMapper.map(appUser, UserDto.class);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/byUsername")
    @ApiOperation(value = "Get user with roles by username", response = UserDto.class)
    public ResponseEntity<UserDto> getUserByUsername(@RequestParam String username) {
        AppUser appUser = appUserService.loadUserByUsername(username);
        UserDto userDto = modelMapper.map(appUser, UserDto.class);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping
    @ApiOperation(value = "Add a new user", response = UserDto.class)
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid CreateUserCommand command) {
        AppUser appUserForSave = appUserService.addUser(command);
        UserDto userDto = modelMapper.map(appUserForSave, UserDto.class);
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/{userId}")
    @ApiOperation(value = "Update password of user defined by ID", response = UserDto.class)
    public ResponseEntity<UserDto> updatePassword(@PathVariable("userId") Long userId,
                                                  @RequestBody @Valid UpdateUserPasswordCommand command) {
        AppUser updatedAppUser = appUserService.updateAppUserPassword(userId, command);
        UserDto userDto = modelMapper.map(updatedAppUser, UserDto.class);
        return ResponseEntity.ok(userDto);
    }

    @DeleteMapping("/{userId}")
    @ApiOperation(value = "Delete user by ID", response = StatusDto.class)
    public ResponseEntity<StatusDto> deleteUserById(@PathVariable("userId") Long userId) {
        appUserService.deleteUserById(userId);
        return ResponseEntity.ok(new StatusDto("User with id " + userId + " deleted."));
    }

    @PostMapping("/{userId}/roles/{roleId}")
    @ApiOperation(value = "Assign a role defined by ID to existing user defined by ID", response = UserDto.class)
    public ResponseEntity<UserDto> assignRoleToUser(@PathVariable Long userId, @PathVariable Long roleId) {
        AppUser updatedAppUser = appUserService.assignRoleToUser(userId, roleId);
        UserDto userDto = modelMapper.map(updatedAppUser, UserDto.class);
        return ResponseEntity.ok(userDto);
    }

    @DeleteMapping("/{userId}/roles/{roleId}")
    @ApiOperation(value = "Remove a role defined by ID from user defined by ID", response = UserDto.class)
    public ResponseEntity<UserDto> removeRoleFromUser(@PathVariable Long userId, @PathVariable Long roleId) {
        AppUser updatedAppUser = appUserService.removeRoleFromUser(userId, roleId);
        UserDto userDto = modelMapper.map(updatedAppUser, UserDto.class);
        return ResponseEntity.ok(userDto);
    }

}
