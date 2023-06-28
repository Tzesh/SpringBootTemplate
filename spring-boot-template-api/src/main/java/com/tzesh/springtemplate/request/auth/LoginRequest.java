package com.tzesh.springtemplate.request.auth;

import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

/**
 * @author tzesh
 */
public record LoginRequest(
        @NotEmpty @Length(min = 3, max = 50) String username,
        @NotEmpty @Length(min = 8, max = 128) String password)
{ }
