package com.enjoythecode.personservice.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.enjoythecode.personservice.dto.UserDto;

/**
 * This Feign client allows you to access user-related data from the 'user-service'.
 * The 'user-service' provides user information, including user details and assigned roles.
 */
@FeignClient(name = "user-service", url = "http://localhost:8081")
public interface UserServiceClient {

    /**
     * Retrieves user information by username.
     *
     * @param username The username of the user to retrieve.
     * @return A UserDto representing the user details.
     */
    @GetMapping("/api/users/byUsername")
    UserDto getUserByUsername(@RequestParam String username);

}
