package com.tzesh.springtemplate.dto;

import com.tzesh.springtemplate.base.dto.BaseDTO;
import com.tzesh.springtemplate.enumeration.auth.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

/**
 * @author tzesh
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDTO extends BaseDTO {
    private Long id;

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
