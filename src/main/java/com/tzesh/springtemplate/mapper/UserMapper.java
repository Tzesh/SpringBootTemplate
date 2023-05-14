package com.tzesh.springtemplate.mapper;

import com.tzesh.springtemplate.base.mapper.BaseMapper;
import com.tzesh.springtemplate.dto.UserDTO;
import com.tzesh.springtemplate.entity.User;
import com.tzesh.springtemplate.request.auth.RegisterRequest;
import org.mapstruct.Mapper;

/**
 * Mapper for {@link User}
 * @see BaseMapper
 * @author tzesh
 */
@Mapper
public interface UserMapper extends BaseMapper<User, UserDTO> {
    public UserDTO toDTO(RegisterRequest request);
}
