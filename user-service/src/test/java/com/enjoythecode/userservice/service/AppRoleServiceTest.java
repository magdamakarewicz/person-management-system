package com.enjoythecode.userservice.service;

import com.enjoythecode.userservice.command.CreateRoleCommand;
import com.enjoythecode.userservice.command.UpdateRoleCommand;
import com.enjoythecode.userservice.exception.InvalidIdException;
import com.enjoythecode.userservice.exception.RoleNotFoundException;
import com.enjoythecode.userservice.model.AppRole;
import com.enjoythecode.userservice.model.AppUser;
import com.enjoythecode.userservice.repository.AppRoleRepository;
import com.enjoythecode.userservice.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AppRoleServiceTest {

    @Mock
    private AppRoleRepository appRoleRepository;

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private AppRoleService appRoleService;

    private AppRole role;

    private List<AppRole> roles;

    private List<AppUser> users;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        role = new AppRole();
        role.setId(1L);
        role.setName("Test Role");

        roles = new ArrayList<>();
        roles.add(role);

        users = new ArrayList<>();
        users.add(new AppUser("user1", "password"));
    }

    @Test
    public void shouldReturnRoleById() {
        //given
        when(appRoleRepository.findById(1L)).thenReturn(Optional.of(role));

        //when
        AppRole foundRole = appRoleService.getRoleById(1L);

        //then
        assertEquals(role, foundRole);
    }

    @Test
    public void shouldThrowInvalidIdExceptionWhenIdIsNull() {
        //when/then
        InvalidIdException exception = assertThrows(InvalidIdException.class,
                () -> appRoleService.getRoleById(null));
        assertEquals("Id cannot be null.", exception.getMessage());
    }

    @Test
    public void shouldThrowRoleNotFoundExceptionWhenRoleNotFound() {
        //given
        when(appRoleRepository.findById(1L)).thenReturn(Optional.empty());

        //when/then
        RoleNotFoundException exception = assertThrows(RoleNotFoundException.class,
                () -> appRoleService.getRoleById(1L));
        assertEquals("Role with id 1 not found.", exception.getMessage());
    }

    @Test
    public void shouldReturnAllRoles() {
        //given
        when(appRoleRepository.findAll()).thenReturn(roles);

        //when
        List<AppRole> foundRoles = appRoleService.getAllRoles();

        //then
        assertEquals(roles, foundRoles);
    }

    @Test
    public void shouldAddNewRole() {
        //given
        CreateRoleCommand command = new CreateRoleCommand("New Role");
        when(appRoleRepository.save(any(AppRole.class))).thenReturn(role);

        //when
        AppRole savedRole = appRoleService.addRole(command);

        //then
        assertEquals(role, savedRole);
    }

    @Test
    public void shouldUpdateRoleName() {
        //given
        UpdateRoleCommand command = new UpdateRoleCommand("Updated Role");
        when(appRoleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(appRoleRepository.save(role)).thenReturn(role);

        //when
        AppRole updatedRole = appRoleService.updateRoleName(1L, command);

        //then
        assertEquals("Updated Role", updatedRole.getName());
    }

    @Test
    public void shouldDeleteRoleById() {
        //given
        when(appRoleRepository.findById(1L)).thenReturn(Optional.of(role));

        //when
        appRoleService.deleteRoleById(1L);

        //then
        verify(appRoleRepository, times(1)).delete(role);
    }

    @Test
    public void shouldReturnUsersByRole() {
        //given
        when(appUserRepository.findUsersByAppRoleId(1L)).thenReturn(users);

        //when
        List<AppUser> foundUsers = appRoleService.getUsersByRole(1L);

        //then
        assertEquals(users, foundUsers);
    }

}