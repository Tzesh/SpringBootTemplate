package com.tzesh.springtemplate.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

/**
 * @author tzesh
 */
public record RegisterRequest(
        @NotEmpty @Length(min = 3, max = 50) String username,
        @NotEmpty @Length(min = 8, max = 128) String password,
        @NotEmpty @Email String email,
        @NotEmpty @Length(min = 3, max = 50) String name)
{ }
