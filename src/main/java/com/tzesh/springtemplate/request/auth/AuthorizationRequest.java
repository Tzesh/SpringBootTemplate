package com.tzesh.springtemplate.request.auth;

import com.tzesh.springtemplate.enums.auth.RoleEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

/**
 * @author tzesh
 */
public record AuthorizationRequest(
        @NotEmpty @NotBlank @Length(min = 3, max = 50) String username,
        @NotEmpty @NotBlank RoleEnum role,
        @NotEmpty @NotBlank @Length(min = 128, max = 128) String secret)
{ }
