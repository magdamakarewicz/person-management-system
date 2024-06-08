package com.enjoythecode.userservice.controller;

import com.enjoythecode.userservice.command.CreateUserCommand;
import com.enjoythecode.userservice.command.UpdateUserPasswordCommand;
import com.enjoythecode.userservice.dto.StatusDto;
import com.enjoythecode.userservice.dto.UserDto;
import com.enjoythecode.userservice.dto.UserSimpleDto;
import com.enjoythecode.userservice.exception.handler.ExceptionResponseBody;
import com.enjoythecode.userservice.model.AppUser;
import com.enjoythecode.userservice.service.AppUserService;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "Users", description = "Operations related to users management")
public class AppUserController {

    private final AppUserService appUserService;

    private final ModelMapper modelMapper;

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema( schema = @Schema(implementation = UserSimpleDto.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class)))
    })
    public ResponseEntity<List<UserSimpleDto>> getAllUsers() {
        return ResponseEntity.ok(appUserService.getAllUsers().stream()
                .map(x -> modelMapper.map(x, UserSimpleDto.class))
                .collect(Collectors.toList()));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID", description = "Retrieve a user by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid ID or Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class)))
    })
    public ResponseEntity<UserDto> getUserById(@PathVariable("userId") Long userId) {
        AppUser appUser = appUserService.getUserById(userId);
        UserDto userDto = modelMapper.map(appUser, UserDto.class);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/byUsername")
    @Operation(summary = "Get user with roles by username", description = "Retrieve a user with roles by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class)))
    })
    public ResponseEntity<UserDto> getUserByUsername(@RequestParam String username) {
        AppUser appUser = appUserService.loadUserByUsername(username);
        UserDto userDto = modelMapper.map(appUser, UserDto.class);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping
    @Operation(summary = "Add a new user", description = "Create a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class)))
    })
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid CreateUserCommand command) {
        AppUser appUserForSave = appUserService.addUser(command);
        UserDto userDto = modelMapper.map(appUserForSave, UserDto.class);
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Update password of user defined by ID", description = "Update the password of an existing user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password successfully updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid ID or Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class)))
    })
    public ResponseEntity<UserDto> updatePassword(@PathVariable("userId") Long userId,
                                                  @RequestBody @Valid UpdateUserPasswordCommand command) {
        AppUser updatedAppUser = appUserService.updateAppUserPassword(userId, command);
        UserDto userDto = modelMapper.map(updatedAppUser, UserDto.class);
        return ResponseEntity.ok(userDto);
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user by ID", description = "Delete a user by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully deleted",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StatusDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid ID or Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class)))
    })
    public ResponseEntity<StatusDto> deleteUserById(@PathVariable("userId") Long userId) {
        appUserService.deleteUserById(userId);
        return ResponseEntity.ok(new StatusDto("User with id " + userId + " deleted."));
    }

    @PostMapping("/{userId}/roles/{roleId}")
    @Operation(summary = "Assign a role defined by ID to existing user defined by ID",
            description = "Assign a role to an existing user by their IDs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role successfully assigned",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid ID or Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class))),
            @ApiResponse(responseCode = "404", description = "User or Role not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class)))
    })
    public ResponseEntity<UserDto> assignRoleToUser(@PathVariable Long userId, @PathVariable Long roleId) {
        AppUser updatedAppUser = appUserService.assignRoleToUser(userId, roleId);
        UserDto userDto = modelMapper.map(updatedAppUser, UserDto.class);
        return ResponseEntity.ok(userDto);
    }

    @DeleteMapping("/{userId}/roles/{roleId}")
    @Operation(summary = "Remove a role defined by ID from user defined by ID",
            description = "Remove a role from an existing user by their IDs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role successfully removed",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid ID or Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class))),
            @ApiResponse(responseCode = "404", description = "User or Role not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseBody.class)))
    })
    public ResponseEntity<UserDto> removeRoleFromUser(@PathVariable Long userId, @PathVariable Long roleId) {
        AppUser updatedAppUser = appUserService.removeRoleFromUser(userId, roleId);
        UserDto userDto = modelMapper.map(updatedAppUser, UserDto.class);
        return ResponseEntity.ok(userDto);
    }

}
