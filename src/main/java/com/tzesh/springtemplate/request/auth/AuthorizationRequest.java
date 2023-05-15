package com.tzesh.springtemplate.request.auth;

import com.tzesh.springtemplate.enums.auth.RoleEnum;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

/**
 * @author tzesh
 */
public record AuthorizationRequest(
        @NotNull @Length(min = 3, max = 50) String username,
        @NotNull RoleEnum role,
        @NotNull @Length(min = 128, max = 128) String secret)
{ }
