package com.enjoythecode.userservice.command;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.enjoythecode.userservice.validator.Password;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserCommand {

    @NotBlank
    private String username;

    @Password
    private String password;

}
