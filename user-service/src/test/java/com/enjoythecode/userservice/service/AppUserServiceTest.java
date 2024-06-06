package com.enjoythecode.userservice.service;

import com.enjoythecode.userservice.command.CreateUserCommand;
import com.enjoythecode.userservice.command.UpdateUserPasswordCommand;
import com.enjoythecode.userservice.exception.InvalidIdException;
import com.enjoythecode.userservice.exception.UserNotFoundException;
import com.enjoythecode.userservice.model.AppRole;
import com.enjoythecode.userservice.model.AppUser;
import com.enjoythecode.userservice.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AppUserServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private AppRoleService appRoleService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AppUserService appUserService;

    private AppUser user;

    private List<AppUser> users;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new AppUser("user1", "password");

        users = new ArrayList<>();
        users.add(user);
    }

    @Test
    public void shouldReturnUserById() {
        //given
        when(appUserRepository.findByIdWithRoles(1L)).thenReturn(Optional.of(user));

        //when
        AppUser foundUser = appUserService.getUserById(1L);

        //then
        assertEquals(user, foundUser);
    }

    @Test
    public void shouldThrowInvalidIdExceptionWhenIdIsNull() {
        //when/then
        InvalidIdException exception = assertThrows(InvalidIdException.class,
                () -> appUserService.getUserById(null));
        assertEquals("Id cannot be null.", exception.getMessage());
    }

    @Test
    public void shouldThrowUserNotFoundExceptionWhenUserNotFound() {
        //given
        when(appUserRepository.findByIdWithRoles(1L)).thenReturn(Optional.empty());

        //when/then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> appUserService.getUserById(1L));
        assertEquals("User with id 1 not found.", exception.getMessage());
    }

    @Test
    public void shouldReturnAllUsers() {
        //given
        when(appUserRepository.findAll()).thenReturn(users);

        //when
        List<AppUser> foundUsers = appUserService.getAllUsers();

        //then
        assertEquals(users, foundUsers);
    }

    @Test
    public void shouldAddUser() {
        //given
        CreateUserCommand command = new CreateUserCommand("user1", "password");

        when(appUserRepository.save(any())).thenReturn(user);

        //when
        AppUser addedUser = appUserService.addUser(command);

        //then
        assertEquals(user, addedUser);
        assertEquals("password", addedUser.getPassword());
    }

    @Test
    public void shouldUpdateUserPassword() {
        //given
        UpdateUserPasswordCommand command = new UpdateUserPasswordCommand("newPassword");

        when(passwordEncoder.encode(command.getPassword())).thenReturn("encodedNewPassword");
        when(appUserRepository.findByIdWithRoles(1L)).thenReturn(Optional.of(user));
        when(appUserRepository.save(any())).thenReturn(user);

        //when
        AppUser updatedUser = appUserService.updateAppUserPassword(1L, command);

        //then
        assertEquals(user, updatedUser);
        assertEquals("encodedNewPassword", updatedUser.getPassword());
    }

    @Test
    public void shouldDeleteUserById() {
        //given
        when(appUserRepository.findByIdWithRoles(1L)).thenReturn(Optional.of(user));

        //when
        appUserService.deleteUserById(1L);

        //then
        verify(appUserRepository, times(1)).delete(user);
    }

    @Test
    public void shouldAssignRoleToUser() {
        //given
        AppRole role = new AppRole();
        role.setId(1L);
        role.setName("ROLE_USER");

        when(appUserRepository.findByIdWithRoles(1L)).thenReturn(Optional.of(user));
        when(appRoleService.getRoleById(1L)).thenReturn(role);
        when(appUserRepository.save(any())).thenReturn(user);

        //when
        AppUser updatedUser = appUserService.assignRoleToUser(1L, 1L);

        //then
        assertTrue(updatedUser.getAppRoles().contains(role));
    }

    @Test
    public void shouldRemoveRoleFromUser() {
        //given
        AppRole role = new AppRole();
        role.setId(1L);
        role.setName("ROLE_USER");
        user.getAppRoles().add(role);

        when(appUserRepository.findByIdWithRoles(1L)).thenReturn(Optional.of(user));
        when(appRoleService.getRoleById(1L)).thenReturn(role);
        when(appUserRepository.save(any())).thenReturn(user);

        //when
        AppUser updatedUser = appUserService.removeRoleFromUser(1L, 1L);

        //then
        assertFalse(updatedUser.getAppRoles().contains(role));
    }

    @Test
    public void shouldLoadUserByUsername() {
        //given
        when(appUserRepository.findByUsernameWithRoles("user1")).thenReturn(Optional.of(user));

        //when
        AppUser foundUser = appUserService.loadUserByUsername("user1");

        //then
        assertEquals(user, foundUser);
    }

}