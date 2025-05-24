package com.tzesh.springtemplate.request.auth;

import com.tzesh.springtemplate.enumeration.auth.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

/**
 * @author tzesh
 */
public record AuthorizationRequest(
        @NotEmpty @NotBlank @Length(min = 3, max = 50) String username,
        @NotNull Role role,
        @NotEmpty @NotBlank @Length(min = 128, max = 128) String secret)
{ }
