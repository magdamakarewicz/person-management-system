package com.enjoythecode.personservice.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.enjoythecode.personservice.api.UserServiceClient;
import com.enjoythecode.personservice.dto.UserDto;
import com.enjoythecode.personservice.model.AppUser;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * This service provides user details for authentication purposes.
 * It utilizes the 'user-service' Feign client to fetch user information.
 */
@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {

    private final UserServiceClient userServiceClient;

    /**
     * Loads user details by username for authentication.
     *
     * @param username The username of the user to load.
     * @return UserDetails representing the loaded user.
     * @throws UsernameNotFoundException If the user is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDto userDto = userServiceClient.getUserByUsername(username);
        if (userDto == null)
            throw new UsernameNotFoundException("User not found: " + username);
        AppUser appUser = new AppUser(
                userDto.getUsername(),
                userDto.getPassword(),
                Arrays.stream(userDto.getRoles())
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList())
        );
        return appUser;
    }

}
