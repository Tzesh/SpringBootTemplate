package com.tzesh.springtemplate.request.user;

import com.tzesh.springtemplate.enums.auth.RoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

/**
 * @author tzesh
 */
public record CreateUserRequest(
        @NotEmpty @Length(min = 3, max = 50) String username,
        @NotEmpty @Length(min = 8, max = 128) String password,
        @NotEmpty @Email String email,
        @NotEmpty @Length(min = 3, max = 50) String name,
        @NotEmpty RoleEnum role)
{ }
