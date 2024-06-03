package com.enjoythecode.userservice.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.enjoythecode.userservice.validator.Password;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserPasswordCommand {

    @Password
    private String password;

}
