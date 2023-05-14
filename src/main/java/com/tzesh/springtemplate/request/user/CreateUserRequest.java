package com.tzesh.springtemplate.request.user;

import com.tzesh.springtemplate.enumerator.auth.RoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

/**
 * @author tzesh
 */
public record CreateUserRequest(
        @NotNull @Length(min = 3, max = 50) String username,
        @NotNull @Length(min = 8, max = 128) String password,
        @NotNull @Email String email,
        @NotNull @Length(min = 3, max = 50) String name,
        @NotNull RoleEnum role)
{ }
