package com.enjoythecode.userservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.enjoythecode.userservice.command.CreateUserCommand;
import com.enjoythecode.userservice.command.UpdateUserPasswordCommand;
import com.enjoythecode.userservice.exception.InvalidIdException;
import com.enjoythecode.userservice.exception.RoleAlreadyAssignedException;
import com.enjoythecode.userservice.exception.RoleNotFoundException;
import com.enjoythecode.userservice.exception.UserNotFoundException;
import com.enjoythecode.userservice.model.AppRole;
import com.enjoythecode.userservice.model.AppUser;
import com.enjoythecode.userservice.repository.AppUserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository appUserRepository;

    private final AppRoleService appRoleService;

    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public AppUser getUserById(Long id) {
        return appUserRepository.findByIdWithRoles(
                Optional.ofNullable(id)
                        .orElseThrow(() -> new InvalidIdException("Id cannot be null."))
        ).orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found."));
    }

    @Transactional(readOnly = true)
    public List<AppUser> getAllUsers() {
        return appUserRepository.findAll();
    }

    @Transactional
    public AppUser addUser(CreateUserCommand command) {
        AppUser appUserForSave = new AppUser();
        appUserForSave.setUsername(command.getUsername());
        appUserForSave.setPassword(passwordEncoder.encode(command.getPassword()));
        return appUserRepository.save(appUserForSave);
    }

    @Transactional
    public AppUser updateAppUserPassword(Long userId, UpdateUserPasswordCommand command) {
        AppUser appUser = getUserById(userId);
        appUser.setPassword(passwordEncoder.encode(command.getPassword()));
        return appUserRepository.save(appUser);
    }

    public void deleteUserById(Long id) {
        AppUser appUserToDelete = getUserById(id);
        appUserRepository.delete(appUserToDelete);
    }

    @Transactional
    public AppUser assignRoleToUser(Long userId, Long roleId) {
        AppUser appUser = getUserById(userId);
        AppRole appRole = appRoleService.getRoleById(roleId);
        if (appUser.getAppRoles().contains(appRole))
            throw new RoleAlreadyAssignedException("Role '" + appRole.getName() + "' is already assigned to the user.");
        appUser.getAppRoles().add(appRole);
        return appUserRepository.save(appUser);
    }

    @Transactional
    public AppUser removeRoleFromUser(Long userId, Long roleId) {
        AppUser appUser = getUserById(userId);
        AppRole appRole = appRoleService.getRoleById(roleId);
        if (!appUser.getAppRoles().contains(appRole))
            throw new RoleNotFoundException("User does not have the '" + appRole.getName() + "' role.");
        appUser.getAppRoles().remove(appRole);
        return appUserRepository.save(appUser);
    }

    public AppUser loadUserByUsername(String username) {
        return appUserRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new UserNotFoundException("User with username '" + username + "' not found."));
    }

}
