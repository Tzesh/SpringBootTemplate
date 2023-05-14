package com.tzesh.springtemplate.request.auth;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

/**
 * @author tzesh
 */
public record LoginRequest(
        @NotNull @Length(min = 3, max = 50) String username,
        @NotNull @Length(min = 8, max = 128) String password)
{ }
