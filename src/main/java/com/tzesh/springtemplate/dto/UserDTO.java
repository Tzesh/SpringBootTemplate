package com.tzesh.springtemplate.dto;

import com.tzesh.springtemplate.base.dto.BaseDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

/**
 * @author tzesh
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO implements BaseDTO {
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
    private String role;
}
