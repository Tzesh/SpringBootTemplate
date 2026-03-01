package com.tzesh.springtemplate.dto;

import com.tzesh.springtemplate.base.dto.BaseDTO;
import com.tzesh.springtemplate.enumeration.auth.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.UUID;

/**
 * @author tzesh
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class UserDTO extends BaseDTO {
    private UUID id;

    @NotNull
    @Length(min = 3, max = 50)
    private String username;

    @Email
    @Length(min = 3, max = 50)
    private String email;

    @NotNull
    @Length(min = 3, max = 50)
    private String name;

    @NotNull
    private Role role;
}
